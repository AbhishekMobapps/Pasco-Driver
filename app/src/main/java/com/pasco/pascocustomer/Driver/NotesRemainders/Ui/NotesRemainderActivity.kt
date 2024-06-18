package com.pasco.pascocustomer.Driver.NotesRemainders.Ui

import android.annotation.SuppressLint
import android.app.*
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.NotesRemainders.ViewModel.NotesRViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityNotesRemainderBinding
import com.pasco.pascocustomer.reminder.ReminderAdapter
import com.pasco.pascocustomer.reminder.ReminderItemClick
import com.pasco.pascocustomer.reminder.ReminderModelView
import com.pasco.pascocustomer.reminder.ReminderResponse
import com.pasco.pascocustomer.reminder.delete.DeleteReminderModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class NotesRemainderActivity : AppCompatActivity(), ReminderItemClick {
    private lateinit var binding: ActivityNotesRemainderBinding
    private lateinit var activity: Activity
    private val notesRViewModel: NotesRViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var formattedDate = ""
    private var formattedDateString = ""
    private var formattedTime = ""


    //
    private val reminderModelView: ReminderModelView by viewModels()
    private val deleteReminderModelView: DeleteReminderModelView by viewModels()
    private var reminderAdapter: ReminderAdapter? = null
    private var reminderList: List<ReminderResponse.Datum>? = ArrayList()
    var dialog: Dialog? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesRemainderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backArrowAddNotes.setOnClickListener {
            finish()
        }
        activity = this

        binding.addReminderBtn.setOnClickListener { showAddReminderPopup() }

        getReminderApi()
        reminderObserver()

    }


    private fun notesReminderApi(addSubjectEdittext: EditText, commentAddNotesReminder: EditText) {
        val title = addSubjectEdittext.text.toString()
        val desp = commentAddNotesReminder.text.toString()

        val reminderDate = "$formattedDateString $formattedTime"
        notesRViewModel.getNotesReminderData(
            progressDialog,
            activity,
            title,
            desp,
            reminderDate
        )
    }

    private fun notesReminderObserver() {
        notesRViewModel.progressIndicator.observe(this, Observer {
        })
        notesRViewModel.mGetNotesResponse.observe(this) { response ->
            val message = response.peekContent().msg
            val success = response.peekContent().status
            if (success == "False") {
                // Handle unsuccessful login

                Toast.makeText(this@NotesRemainderActivity, message, Toast.LENGTH_SHORT).show()


            } else {

                getReminderApi()
                dialog?.dismiss()
                Toast.makeText(this@NotesRemainderActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        notesRViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this@NotesRemainderActivity, it)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddReminderPopup() {
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.add_reminder)


        val startDateTxtNotes = dialog?.findViewById<TextView>(R.id.startDateTxtNotes)
        val startTimetxtNotes = dialog?.findViewById<TextView>(R.id.startTimetxtNotes)
        val addSubjectEdittext = dialog?.findViewById<EditText>(R.id.addSubjectEdittext)
        val commentAddNotesReminder = dialog?.findViewById<EditText>(R.id.commentAddNotesReminder)
        val saveBtnAddNotes = dialog?.findViewById<LinearLayout>(R.id.saveBtnAddNotes)
        val backArrowAddNotes = dialog?.findViewById<ImageView>(R.id.backArrowAddNotes)


        backArrowAddNotes?.setOnClickListener { dialog?.dismiss() }
        saveBtnAddNotes?.setOnClickListener {

            validation(startDateTxtNotes!!, startTimetxtNotes!!, addSubjectEdittext!!, commentAddNotesReminder!!)
        }

        startDateTxtNotes?.setOnClickListener {
            val calendar = Calendar.getInstance()

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@NotesRemainderActivity, R.style.MyTimePicker,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val formatDay = String.format("%02d", dayOfMonth)
                    formattedDate = "$year-$formattedMonth-$formatDay"

                    val originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val desiredFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                    // Parse the original date string to a LocalDate
                    val date = LocalDate.parse(formattedDate, originalFormatter)

                    // Format the LocalDate to the desired string format
                    formattedDateString = date.format(desiredFormatter)
                    startDateTxtNotes.text = formattedDateString
                },
                year, month, day
            )

            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        startTimetxtNotes?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Do something with the selected time
                    val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                    val amPm = if (selectedHour < 12) "AM" else "PM"
                    formattedTime =
                        String.format("%02d:%02d%s", formattedHour, selectedMinute, amPm)
                    startTimetxtNotes.text = formattedTime

                    Log.e("formattedDateString", "selectedTime..." + formattedTime)
                },
                hour,
                minute,
                false // Set to false for 12-hour format with AM/PM
            )
            timePickerDialog.show()
        }

        val window = dialog?.window
        val lp = window?.attributes
        if (lp != null) {
            lp.width = ActionBar.LayoutParams.MATCH_PARENT
        }
        if (lp != null) {
            lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        }
        if (window != null) {
            window.attributes = lp
        }


        dialog?.show()
    }

    private fun validation(
        startDateTxtNotes: TextView,
        startTimetxtNotes: TextView,
        addSubjectEdittext: EditText,
        commentAddNotesReminder: EditText
    ) {
        if (startDateTxtNotes.text.toString().isNullOrBlank()) {
            Toast.makeText(this@NotesRemainderActivity, "Please select date", Toast.LENGTH_SHORT)
                .show()
        } else if (startTimetxtNotes.text.toString().isNullOrBlank()) {
            Toast.makeText(this@NotesRemainderActivity, "Please select time", Toast.LENGTH_SHORT)
                .show()
        } else if (addSubjectEdittext.text.toString().isNullOrBlank()) {
            Toast.makeText(
                this@NotesRemainderActivity,
                "Please add the subject",
                Toast.LENGTH_SHORT
            ).show()
        } else if (commentAddNotesReminder.text.isNullOrBlank()) {
            Toast.makeText(this@NotesRemainderActivity, "Please add the description", Toast.LENGTH_SHORT).show()
        } else {
            //call api
            notesReminderApi(addSubjectEdittext, commentAddNotesReminder)
            notesReminderObserver()
        }
    }

    private fun getReminderApi() {
        reminderModelView.getReminder(
            this,
            progressDialog
        )
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun reminderObserver() {
        reminderModelView.progressIndicator.observe(this@NotesRemainderActivity, Observer {
        })

        reminderModelView.mRejectResponse.observe(this@NotesRemainderActivity) { response ->
            val message = response.peekContent().msg!!
            val success = response.peekContent().status
            reminderList = response.peekContent().data!!

            Log.e("NotificationListAA", "aaa" + reminderList!!.size)
            if (success == "True") {
                if (reminderList!!.isEmpty()) {
                    binding.noDataFoundTxt.visibility = View.VISIBLE

                } else {
                    binding.constRecycler.visibility = View.VISIBLE
                    binding.addNotesRecycler.isVerticalScrollBarEnabled = true
                    binding.addNotesRecycler.isVerticalFadingEdgeEnabled = true
                    binding.addNotesRecycler.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    reminderAdapter = ReminderAdapter(this, this, reminderList!!)
                    binding.addNotesRecycler.adapter = reminderAdapter
                    val currentDate = LocalDate.now()
                }


            }
        }

        reminderModelView.errorResponse.observe(this@NotesRemainderActivity) {
            ErrorUtil.handlerGeneralError(this@NotesRemainderActivity, it)
        }
    }

    override fun reminderItemClick(position: Int, id: Int) {
        deleteReminderApi(id)
        deleteReminderObserver()
    }

    private fun deleteReminderApi(id: Int) {
        deleteReminderModelView.deleteReminder(id.toString(), this, progressDialog)
    }

    @SuppressLint("SetTextI18n")
    private fun deleteReminderObserver() {
        deleteReminderModelView.progressIndicator.observe(this@NotesRemainderActivity) {
        }
        deleteReminderModelView.mRejectResponse.observe(this@NotesRemainderActivity) {
            val message = it.peekContent().msg
            val success = it.peekContent().status

        //    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            getReminderApi()
        }
        deleteReminderModelView.errorResponse.observe(this@NotesRemainderActivity) {
            ErrorUtil.handlerGeneralError(this@NotesRemainderActivity, it)
            //errorDialogs()
        }
    }
}