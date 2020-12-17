package com.example.modbustest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.SimpleRegister;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends Activity{

    // The important instances of the classes mentioned before
    TCPMasterConnection con = null;     //the TCP connection
    ModbusTCPTransaction trans = null;  //the Modbus transaction

    // Variables for storing the parameters
    InetAddress addr = null;        //the slave's address
    int port = Modbus.DEFAULT_PORT;

    Button btnRead, btnWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // associate the layout to the activity
        setContentView(R.layout.activity_main);

        // I suppose of having a layout with two simple buttons
        btnRead = (Button) findViewById(R.id.btnRead);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new ReadTask()).start();
            }
        });
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new WriteTask()).start();
            }
        });
    }

    public void connect() {
        try {
            addr = InetAddress.getByName("192.168.1.121");

            // Open the connection
            con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();
            Log.d("MODBUS", "connection normalno");
        } catch (Exception e) {
            Log.d("MODBUS", "connection error", e);
        }
    }

    public  class ReadTask extends Thread{
        public void run(){
            connect();

            Log.d("MODBUS","Вы в чтении");
            int startReg = 0;

            ReadMultipleRegistersRequest req = null; //the request
            ReadMultipleRegistersResponse res = null; //the response

            // Prepare the request
            req = new ReadMultipleRegistersRequest(startReg, 0);

            // Prepare the transaction
            trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);

            // execute the transaction
            try {
                trans.execute();
            } catch (ModbusException e) {
                e.printStackTrace();
            }
            // get the response
            res = (ReadMultipleRegistersResponse) trans.getResponse();
        }
    }

    public  class WriteTask extends Thread{
        public void run(){
            connect();
            Log.d("MODBUS","Вы в записи");
            int startReg = 3;               //writes the fourth register

            WriteMultipleRegistersRequest req = null; //the request
            WriteMultipleRegistersResponse res = null; //the response

            // Prepare the request and create a simple integer register
            SimpleRegister[] hr = new SimpleRegister[1];
            hr[0]=new SimpleRegister(65);

            req = new WriteMultipleRegistersRequest(startReg, hr);

            // Prepare the transaction
            trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);

            //execute the transaction
            try {
                trans.execute();
            } catch (ModbusException e) {
                e.printStackTrace();
            }
            res = (WriteMultipleRegistersResponse) trans.getResponse();

        }
     }

    }