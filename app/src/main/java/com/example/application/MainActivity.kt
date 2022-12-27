package com.example.application

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    lateinit var linelist: ArrayList<Entry>
    lateinit var  linedataset: LineDataSet
    lateinit var lineData: LineData

    fun encryptThisString(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val messageDigest = md.digest(input.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val refreshButton =  findViewById<Button>(R.id.refresh)
        val resultTextView= findViewById<TextView>(R.id.resultview)
        val dbTextView= findViewById<TextView>(R.id.tempidtextView)
        //val xValusView= findViewById<TextView>(R.id.textView)
        //val line_Chart= findViewById<LineChart>(R.id.line_Chart)
        var database = FirebaseDatabase.getInstance().reference
        val xValues = ArrayList<String>()
        val yValues = ArrayList<String>()
        val yVal = ArrayList<Any>()


        /*refreshButton.setOnClickListener {
            //val rand = Random.nextInt(100)
            //resultTextView.text = rand.toString() //for testing
           //database.child("A4").setValue(Employee("1260", "90"))
        }*/
        var getdata= object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                var sb = StringBuilder()
                for (i in p0.children){
                    var timestmp1= i.child("TimeStamp").getValue()
                    var watrat1= i.child("waterlevel").getValue()
                    var hashval1= i.child("Hash").getValue()

                    var resultofhash= encryptThisString(watrat1.toString()+"WATER"+timestmp1.toString())
                    if (resultofhash == hashval1.toString()){
                        hashval1= "True"
                    }
                    else {hashval1= "False"

                    }
                    sb.append("TimeStamp: $timestmp1 WaterLevel: $watrat1 ml\nHash: $hashval1\n") //Hash: $hashval1

                    xValues.add(timestmp1.toString())
                    yValues.add(watrat1.toString())
                    if (watrat1 != null) {
                        yVal.add(watrat1)
                    }

                }
                dbTextView.setText(sb)
                refreshButton.setOnClickListener {
                    val reultt =
                        ( (yValues[yValues.size - 1].toDouble() - yValues[1].toDouble()) / (xValues[xValues.size - 1].toDouble()
                                - xValues[1].toDouble()))
                    resultTextView.setText(reultt.toString() )
                    /*
                    if (reultt > 3.6) {
                        xValusView.setText("The  rate is higher than normal. You have to take precautions")
                    } else {
                        xValusView.setText("The rate is safe")
                    }*/
                }
            }
        }
        database.addValueEventListener(getdata)
        database.addListenerForSingleValueEvent(getdata)
        /*
        linelist = ArrayList()

        linelist.add(Entry(00f ,9.17f))
        linelist.add(Entry(30f , 13.7f))
        linelist.add(Entry(60f , 20.6f))
        linelist.add(Entry(90f , 33.4f))




        linedataset = LineDataSet(linelist, "Water level")
        lineData = LineData(linedataset)
        line_Chart.data= lineData
        linedataset.color = Color.BLACK
        linedataset.valueTextColor = Color.BLUE
        linedataset.valueTextSize = 13f
        linedataset.setDrawFilled(true)*/
    }




}