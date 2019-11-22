package bismillah;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author AM
 */
public final class ReadFile {
    String firstLine;
    int event, room, feature, student, totalcapacity;
    int timeslot = 45;
    String[] firstLineArray;
    int[] roomCapacityArray, sizeStudentEvent, countSuitableRoom, countEventFeature;
    int[][] mStudentEvent, mRoomFeatures, mEventFeatures, mSuitableRoom, 
            conflictMatrix, conflictCourse, timeslotRoom, 
            eventSlot, eventOrder, SuitableSlot, SuitableOrder, beforeSlot, afterSlot;
    ArrayList<List<Integer>> CMStudentEvent;
    
    ReadFile(String filename) throws FileNotFoundException, IOException {
//        File file = new File(filename);                                             //read file .tim run via netbeans
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        
        InputStream in = getClass().getResourceAsStream("/resources/"+ filename);       //read file .tim run via .jar
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        
        firstLineArray = bufferedReader.readLine().split(" ");                          //read firstline
        event = Integer.parseInt(firstLineArray[0]);                                    //simpan jumlah kelas
        room =  Integer.parseInt(firstLineArray[1]);                                    //simpan jumlah ruang
        feature = Integer.parseInt(firstLineArray[2]);                                  //simpan jumlah fitur
        student = Integer.parseInt(firstLineArray[3]);                                  //simpan jumlah mahasiswa
        
        roomCapacityArray = new int[room];
        for (int i = 0; i < room; i++) {
            roomCapacityArray[i] = Integer.parseInt(bufferedReader.readLine());         //simpan kapasitas ruang dalam array
        }
        
        for (int rooms : roomCapacityArray) {                                           //menyimpanan total kapasistas ruang
            totalcapacity = totalcapacity + rooms;
        }
        
        mStudentEvent = new int[student][event];                                        //matriks Event x Student
        for (int i = 0; i < student; i++) {
            for (int j = 0; j < event; j++) {
                mStudentEvent[i][j] = Integer.parseInt(bufferedReader.readLine());
            }
        }
        
        mRoomFeatures = new int[room][feature];                                     //matriks Room x Features
        for (int i = 0; i < room; i++) {
            for (int j = 0; j < feature; j++) {
                mRoomFeatures[i][j] = Integer.parseInt(bufferedReader.readLine());
            }
        }
        
        mEventFeatures = new int[event][feature];                                   //matriks Event x Features
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < feature; j++) {
                mEventFeatures[i][j] = Integer.parseInt(bufferedReader.readLine());
            }
        }
        
        sizeStudentEvent = new int[event];                                          //Ukuran event -- event i diikuti n mahasiswa
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < student; j++) {
                sizeStudentEvent[i] = sizeStudentEvent[i] + mStudentEvent[j][i];
            }
        }
        
        //matrix for ITC-2007 Dataset
        eventSlot = new int[event][timeslot];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < timeslot; j++) {
                eventSlot[i][j] = Integer.parseInt(bufferedReader.readLine());
            }
        }
        
        //  matrix for ITC-2007 Dataset
        eventOrder = new int[event][event];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < event; j++) {
                eventOrder[i][j] = Integer.parseInt(bufferedReader.readLine());
            }
        }

        suitableRoom();
        suitableSlot(); /*ITC-2007*/
        suitableOrder(); /*ITC-2007*/
        countSuitableRoom();
        countEventFeatures();
        CMStudentEvent();
    }
    
    //untuk mengecek apakah event-room-feature-student telah sesuai
    void suitableRoom() {
        mSuitableRoom = new int[event][room];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < room; j++) {
                if (roomCapacityArray[j] >= sizeStudentEvent[i]) {                   
                    for (int k = 0; k < feature; k++) {
                        if (mEventFeatures[i][k] == 1 && mRoomFeatures[j][k] == 0) {
                            mSuitableRoom[i][j] = 0;
                        } else if (mEventFeatures[i][k] == 1 && mRoomFeatures[j][k] == 1) {
                            mSuitableRoom[i][j] = 1;
                        } else if (mEventFeatures[i][k] == 0 && mRoomFeatures[j][k] == 1) {
                            mSuitableRoom[i][j] = 1;
                        } else if (mEventFeatures[i][k] == 0 && mRoomFeatures[j][k] == 0) {
                            mSuitableRoom[i][j] = 1;
                        }
                    }
                }
            }
        }
    }
    
     int[][] suitableSlot() {
        SuitableSlot = new int[event][timeslot];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < timeslot; j++) {
                SuitableSlot[i][j]=eventSlot[i][j];
            }
        }
        return SuitableSlot;
     }
     
     int[][] suitableOrder() {
        SuitableOrder = new int[event][event];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < event; j++) {
                SuitableOrder[i][j]=eventOrder[i][j];
            }
        }
        return SuitableOrder;
     }
            
    
    //Untuk menghitung berapa ruang yang available untuk setiap event
    void countSuitableRoom() {
        countSuitableRoom = new int[event];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < room; j++) {
                if (mSuitableRoom[i][j] == 1) {
                    countSuitableRoom[i]++;
                }
            }
        }
    }
    
    void countEventFeatures() {
        countEventFeature = new int[event];
        for (int i = 0; i < event; i++) {
            for (int j = 0; j < feature; j++) {
                if (mEventFeatures[i][j] == 1) {
                    countEventFeature[i]++;
                }
            }
        }
    }
    
    //Untuk mengetahui student ke-i mengambil event apa saja
    void CMStudentEvent() {
        CMStudentEvent = new ArrayList<>(event);
        for (int i = 0; i < student; i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int j = 0; j < event; j++) {
                if (mStudentEvent[i][j]==1) {
                    temp.add(j);
                }
            }
            CMStudentEvent.add(temp);
        }
//        System.out.println(CMStudentEvent);
    }
    
    //conflict matrix digunakan untuk mengetahui event yang bentrok dan berapa jumlah student yang bentrok
    int[][] conflictMatrix() {
        conflictMatrix = new int[event][event];
        for (int i = 0; i < CMStudentEvent.size(); i++) {
            for (int j = 0; j < CMStudentEvent.get(i).size()-1; j++) {
                for (int k = j+1; k < CMStudentEvent.get(i).size(); k++) {
                    int eventi = CMStudentEvent.get(i).get(j);
                    int eventj = CMStudentEvent.get(i).get(k);
//                    System.out.println(eventi+", "+eventj);
                    conflictMatrix[eventi][eventj]++;
                    conflictMatrix[eventj][eventi]++;
//                    System.out.println("student ke "+ i +" conflict matrix " 
//                            + (conflictMatrix[eventi][eventj])+ " , " + (conflictMatrix[eventj][eventi]));
//                    System.out.println("eventi ="+eventi+", eventj="+eventj);
                    
                }
//                System.out.println("CM "+Arrays.toString(conflictMatrix[i]));
            }
            
        }
        return conflictMatrix;
    }
    
    //untuk memasukkan jadwal yang bentrok tanpa jadwal yang tidak bentrok
    int [][]conflictCourse(){
        conflictCourse = new int[event][];
        for (int i = 0; i < event; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for (int j = 0; j < conflictMatrix[i].length; j++) {
                int conflict = conflictMatrix[i][j];
//                System.out.println("int conflict " + conflict);
//                System.out.println("i "+i+"j " + j); //j adalah event ke-
                if (conflict >= 1 && i != j) {
                    temp.add(j);
//                    System.out.println("temp " + temp);
                }
            }
            int[] arrayTemp = new int[temp.size()];
            for (int j = 0; j < temp.size(); j++) {
                arrayTemp[j] = temp.get(j);
//                System.out.println("arrayTemp "+arrayTemp[j]);
            }
            conflictCourse[i] = arrayTemp;
//            System.out.println("cc "+Arrays.toString(conflictCourse[i]));
        }
        return conflictCourse;
    }
    
    int[][] timeslotRoom() {
        timeslotRoom = new int[timeslot][room];
        for (int i = 0; i < timeslotRoom.length; i++) {
            for (int j = 0; j < timeslotRoom[i].length; j++) {
                timeslotRoom[i][j] = 0;
            }
        }
        return timeslotRoom;
    }
    
    int [][]beforeSlot(){
        beforeSlot = new int[event][];
        for (int i = 0; i < event; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for (int j = 0; j < SuitableOrder[i].length; j++){
                int conflict = SuitableOrder[i][j];
//                System.out.println("int conflict " + conflict);
                
                if (conflict > 0 && i != j) {
                    temp.add(j);
//                    System.out.println("temp " + temp);
//                    System.out.println("i "+i+"j " + j); //j adalah event ke-
                }
            }
            int[] arrayTemp = new int[temp.size()];
            for (int j = 0; j < temp.size(); j++) {
                arrayTemp[j] = temp.get(j);
//                System.out.println("arrayTemp "+arrayTemp[j]);
            }
            beforeSlot[i] = arrayTemp;
        }
        return beforeSlot;
    }
    
    int [][]afterSlot(){
        afterSlot = new int[event][];
        for (int i = 0; i < event; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            for (int j = 0; j < SuitableOrder[i].length; j++){
                int conflict = SuitableOrder[i][j];
//                System.out.println("int conflict " + conflict);
//                System.out.println("i "+i+"j " + j); //j adalah event ke-
                if (conflict < 0 && i != j) {
                    temp.add(j);
//                    System.out.println("temp " + temp);
                }
            }
            int[] arrayTemp = new int[temp.size()];
            for (int j = 0; j < temp.size(); j++) {
                arrayTemp[j] = temp.get(j);
//                System.out.println("arrayTemp "+arrayTemp[j]);
            }
            afterSlot[i] = arrayTemp;
        }
        
        return afterSlot;
    }
}
