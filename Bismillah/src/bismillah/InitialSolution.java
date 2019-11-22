package bismillah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author AM
 */
public class InitialSolution {
    ArrayList<Course> course = new ArrayList<>();
    ArrayList<Integer> noTimeslot, reqEvent;
    ArrayList<List<Integer>> conflictTimeslot;
    int[] courseRoom, courseTimeslot, cekAfterSlot;
    int lengthSlot, distFeasibility;;
    
    InitialSolution(int[][] conflictcourse, int[] sizeeventstudent, 
            int[] countsuitableroom, int[]counteventfeatures, int[][] afterslot,
            int timeslot) {
        Random r = new Random();
        int low = 1;
        int high = conflictcourse.length+1;
        for (int i = 0; i < conflictcourse.length; i++) {
            int random = r.nextInt(high-low) + low;
            course.add(new Course(i, conflictcourse[i].length, 
                    sizeeventstudent[i], countsuitableroom[i], 
                    counteventfeatures[i], timeslot, random));
            //menyimpan paket nilai kedalam arraylist
//            System.out.println("course "+i+", cc length "+conflictcourse[i].length+
//                    ", size student "+sizeeventstudent[i]+", suitable room "+
//                    countsuitableroom[i]+", event features "+counteventfeatures[i]+
//                    ", timeslot "+timeslot+", random "+random);
            
        }
        Collections.sort(course, new courseChained(                             //sorting object
                new courseSortingRooms(),
                new courseSortingTimeslot(),
                new courseSortingConflict(),
                new courseSortingRandom()
////                new courseSortingBefSlot()
                ));
        courseTimeslot = new int[sizeeventstudent.length];                      //menyimpan timeslot final
        courseRoom = new int[sizeeventstudent.length];                          //menyimpan room final
    }
    
    //Assign jadwal initial solution ke timeslot dan room yang sesuai
    void exploreSlot(int[][] conflictcourse, int[] sizeeventstudent, int[] countsuitableroom, 
            int[]counteventfeatures, int timeslot, int[][] timeslotrooms, int[][] suitablerooms, 
            int[][] suitableslot, int[][] suitableorder, int[][] beforeslot, int[][] afterslot) {
        Random r = new Random();
        int[] hitunganTimeslot=new int[conflictcourse.length];
        
        for (int i = 0; i < conflictcourse.length; i++) {
            hitunganTimeslot[i]=timeslot;
            if (afterslot[i].length>0 /*|| beforeslot[i].length>0*/){
                reqEvent = new ArrayList<>();
                reqEvent.add(i);
                if (afterslot[i].length>0){
                    for (int j = 0; j < afterslot[i].length; j++) {
                    reqEvent.add(0, afterslot[i][j]);
//                    System.out.println("req eventnya first "+reqEvent);
                    }
                }
                /*if (beforeslot[i].length>0){
                    for (int j = 0; j < beforeslot[i].length; j++) {
                        reqEvent.add(reqEvent.size(), beforeslot[i][j]);
                    }
                }*/
                //cek apakah event before memiliki dependensi harus setelah event apa
                for (int k = 0; k < 1; k++){
                    int[] arrCekAftSlot = reqEvent.stream().mapToInt(a -> a).toArray();
                    int cekslot= arrCekAftSlot[k]; 
//                    System.out.println("event " + cekslot);
                    if (afterslot[cekslot].length>0){
                        for (int j = 0; j < afterslot[cekslot].length; j++) { 
                            reqEvent.add(0, afterslot[cekslot][j]);
//                            System.out.println("event syarat "+afterslot[cekslot][j]); 
                        }
                    }
                }
//                System.out.println("req eventnya "+reqEvent);
                for (int k = 0; k < reqEvent.size(); k++){
                    int[] arr = reqEvent.stream().mapToInt(a -> a).toArray();
                    if (courseTimeslot[arr[k]]==0){
//                      System.out.println("event "+arr[k]);
                        int timeslotk = (timeslot-1);
//                        System.out.println("timeslotk "+timeslotk);
                    
                        //membagi range timeslot antar event
                        int low = k*(timeslotk / reqEvent.size());
                        int high = (k+1)*timeslotk / reqEvent.size();
                        int random = (r.nextInt(high-low) + low);
//                        System.out.println("low "+low);
//                        System.out.println("high "+high);
//                        System.out.println("random "+random);
                        outerloop:  
                        for (int l = low; l < 100; l++){
                            if (searchTS(arr[k], (random+1), conflictcourse, suitableslot)){
//                                System.out.println("event "+arr[k]+"randomnya "+(random+1)); 
                              for (int m = 0; m < timeslotrooms[(random)].length; m++) {
                                  //cek contraint mengenai timeslot dan ruangan
                                  if (cekTimeslotRooms(arr[k], timeslotrooms, suitablerooms, random, m)){
                                      //apabila lolos cek constrain, taruh timeslot dan ruangan
                                      placeTimeslotRooms(arr[k], timeslotrooms, random, m);
                                      hitunganTimeslot[arr[k]]=1000000000;
//                                      System.out.println("event "+arr[k]+"hitungan Timeslotnya "+hitunganTimeslot[arr[k]]);
                                      for (int n = 0; n < conflictcourse[arr[k]].length; n++){
                                          int updAvailTimeslot =  conflictcourse[arr[k]][n];
//                                          System.out.println("updAvailTimeslot "+updAvailTimeslot);
                                          hitunganTimeslot[updAvailTimeslot]--;
                                      }
                                        break outerloop;                              
                                  }
                              }
                            }          
                            random = (r.nextInt(high-low) + low);
                        }
                    }
                }
//                reqEvent.clear();
        
            }
            
        }
//        int index=0;
        for (int i = 0; i < conflictcourse.length; i++) {
            int low = 1;
            int high = conflictcourse.length+1;
            int index = course.get(i).getIndex();
            
//            for (int o = 0; o < conflictcourse.length; o++){
//                System.out.println("index ke "+o+", hitungan timeslotnya"+hitunganTimeslot[o]);
//            }
            for (int o = 0; o < conflictcourse.length; o++){
                int random = r.nextInt(high-low) + low;
                int min = Arrays.stream(hitunganTimeslot).min().getAsInt();
                
                if (hitunganTimeslot[o]==min) {
//                    System.out.println("Min = " + min);
//                    System.out.println("indexnya = " + o);
                    index=o;
                }
//                course.add(new Course(o, conflictcourse[o].length, 
//                    sizeeventstudent[o], countsuitableroom[o], 
//                    counteventfeatures[o], hitunganTimeslot[o], random));
//                System.out.println("course "+o+", cc length "+conflictcourse[o].length+
//                    ", size student "+sizeeventstudent[o]+", suitable room "+
//                    countsuitableroom[o]+", event features "+counteventfeatures[o]+
//                    ", timeslot "+hitunganTimeslot[o]+", random "+random);
//                }
            if (courseTimeslot[index]==0){
//                System.out.println("index "+index);
                outerloop:
                for (int j = 0; j < timeslot; j++) { //yang di loop harusnya timeslotnya yang bisa berubah
                    if (searchTS(index, j+1, conflictcourse, suitableslot)) { //kalau searchTS benar maka
                        for (int k = 0; k < timeslotrooms[j].length; k++) {
                            //cek contraint mengenai timeslot dan ruangan
                            if (cekTimeslotRooms(index, timeslotrooms, suitablerooms, j, k)){
                                //apabila lolos cek constrain, taruh timeslot dan ruangan
                                placeTimeslotRooms(index, timeslotrooms, j, k);
//                              if (orderTS (index, j+1, tr, sr, courseTimeslot, suitableorder)) {
                                hitunganTimeslot[index]=1000000000;
//                                System.out.println("index "+index+"hitungan Timeslotnya "+hitunganTimeslot[index]);
                                for (int n = 0; n < conflictcourse[index].length; n++){
                                    int updAvailTimeslot =  conflictcourse[index][n];
                                    hitunganTimeslot[updAvailTimeslot]--;
//                                    course.clear();
                                }
                                break outerloop;
                            }
                            
                        }
                    }
                }
                }
            }
        }
    }
       
    
    //if eksplore untuk menghindarkan bentrok
    boolean searchTS(int index, int currentSlot, int[][] conflictcourse, 
            int[][] suitableslot) {
        for (int i = 0; i < conflictcourse[index].length; i++) {
            int numCourse = conflictcourse[index][i];
            int slotAccept = suitableslot[index][currentSlot-1];
//            System.out.println("index ke "+ index);
//            System.out.println("numcoursenya adalah "+numCourse);
//            System.out.println("currentslot ke "+currentslot);
//            System.out.println("courseTimeslot "+courseTimeslot[numCourse]);
//            System.out.println("slotAccept "+slotAccept);
                if (slotAccept !=1) {
//                    System.out.println("false");
                    return false;
                }
                else if (slotAccept !=0){
                    if (courseTimeslot[numCourse] == currentSlot) {
//                        System.out.println("false");
                        return false;
                    }
//                    else return true;
                }
        }
        return true;
    }
    
    boolean cekTimeslotRooms(int index, int[][] timeslotrooms, int[][] suitablerooms, int j, int k){
            int tr = timeslotrooms[j][k];
            int sr = suitablerooms[index][k];
            if (tr == 0 && sr == 1) {
                return true;
            }
        return false;
    }
    
    void placeTimeslotRooms(int index, int[][] timeslotrooms, int j, int k){
//        System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
        courseTimeslot[index] = j+1;
        courseRoom[index] = k+1;
//      System.out.println("taruh courseRoom  " +courseRoom[index]);
        timeslotrooms[j][k]++;
    }
    
    //Cek semua event telah terjadwal
    void noTimeslot(int[] sizeeventstudent) {
        noTimeslot = new ArrayList<>();
        for (int i = 0; i < courseTimeslot.length; i++) {
            if (courseTimeslot[i] < 1 || courseRoom[i] < 1) {
//                System.out.println("course ini "+i);
                noTimeslot.add(i);
            }
        }
        distFeasibility(sizeeventstudent);
        System.out.println(distFeasibility);
    }
    
    //Distance to feasibility. cek event yang tidak ditempatkan dan berapa siswanya
    int distFeasibility(int[] sizeStuEv){
        distFeasibility = 0;
        noTimeslot = new ArrayList<>();
        for (int i = 0; i < courseTimeslot.length; i++) {
            if (courseTimeslot[i] < 1 || courseRoom[i] < 1) {
                noTimeslot.add(i);
                distFeasibility= distFeasibility + sizeStuEv[i];
            }
        }
        return distFeasibility;
    }
    
}
