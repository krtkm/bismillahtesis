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
    ArrayList<Integer> noTimeslot, reqEvent, reqMoveEvent, reqSlotOrder;
    ArrayList<List<Integer>> conflictTimeslot;
    int[] courseRoom, courseTimeslot, cekAfterSlot;
    int lengthSlot, distFeasibility, precedenceViolation;
    
    InitialSolution(int[][] conflictcourse, int[] sizeeventstudent, 
            int[] countsuitableroom, int[]counteventfeatures, int[][] afterslot,
            int timeslot, int[] countorderslot) {
        Random r = new Random();
        int low = 1;
        int high = conflictcourse.length+1;
        for (int i = 0; i < conflictcourse.length; i++) {
            int random = r.nextInt(high-low) + low;
            course.add(new Course(i, conflictcourse[i].length, 
                    sizeeventstudent[i], countsuitableroom[i], 
                    counteventfeatures[i], timeslot, countorderslot[i], random));
            //menyimpan paket nilai kedalam arraylist
//            System.out.println("course "+i+", cc length "+conflictcourse[i].length+
//                    ", size student "+sizeeventstudent[i]+", suitable room "+
//                    countsuitableroom[i]+", event features "+counteventfeatures[i]+
//                    ", timeslot "+timeslot+", countOrderSlot "+countorderslot[i]);
            
        }
        Collections.sort(course, new courseChained(                             //sorting object
                new courseSortingStudent(),               
                new courseSortingRooms(),
//                new courseSortingTimeslot(),
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
        int orderTS=0;
//        int[] hitunganTimeslot=new int[conflictcourse.length];
//                do {
        for (int i = 0; i < conflictcourse.length; i++) {
            orderTS=0;
//            hitunganTimeslot[i]=timeslot;
            Collections.sort(course, new courseChained(                             //sorting object
                new courseSortingOrderSlot(),
                new courseSortingStudent(),               
                new courseSortingRooms(),
//                new courseSortingTimeslot(),
                new courseSortingConflict(),
                new courseSortingRandom()
                ));
            int index = course.get(i).getIndex();
            System.out.println("index "+index);
            if (beforeslot[index].length>0 || afterslot[index].length>0){
                reqEvent = new ArrayList<>();
                reqSlotOrder = new ArrayList<>();
                reqEvent.add(index);
                if (beforeslot[index].length>0){
                    for (int j = 0; j < beforeslot[index].length; j++) {
                    reqEvent.add(0, beforeslot[index][j]);
//                    System.out.println("req eventnya first "+reqEvent);
                    }
                }
                if (afterslot[index].length>0){
                    for (int j = afterslot[index].length-1; j < afterslot[index].length; j++) {
                        reqEvent.add(reqEvent.size(), afterslot[index ][j]);
                    }
                }
                //cek apakah event before memiliki dependensi harus setelah event apa
                for (int k = 0; k < 1; k++){
                    int[] arrCekAftSlot = reqEvent.stream().mapToInt(a -> a).toArray();
                    int cekslot= arrCekAftSlot[k]; 
//                    System.out.println("event " + cekslot);
                    if (afterslot[cekslot].length>0){
                        for (int j = 0; j < beforeslot[cekslot].length; j++) { 
                            reqEvent.add(0, beforeslot[cekslot][j]);
//                            System.out.println("event syarat "+afterslot[cekslot][j]); 
                        }
                    }
                }
                System.out.println("req eventnya "+reqEvent);
                for (int k = 0; k < reqEvent.size(); k++){
                    int min = timeslot-2;
                    int max = 0;
                    int[] arr = reqEvent.stream().mapToInt(a -> a).toArray();
                    int timeslotk = (timeslot-1);

                    //membagi range timeslot antar event
                    int low = k*(timeslotk / reqEvent.size());
                    int high = (k+1)*timeslotk / reqEvent.size();
                    int random = (r.nextInt(high-low) + low);
//                    System.out.println("low "+low);
//                    System.out.println("high "+high);
//                    System.out.println("random "+random);
                    if (courseTimeslot[arr[k]]==0){
                        System.out.println("lhooo "+arr[k] + "," +courseTimeslot[arr[k]]);
                        if (k==0){
                            if (courseTimeslot[arr[k+1]]==0){
        //                      System.out.println("event "+arr[k]);
                                System.out.println("tes awal");
                                randomInitialTS(arr[k], random, conflictcourse, timeslotrooms, suitablerooms,
                                        timeslot, suitableslot, afterslot, beforeslot, r , low, high);
                            }
                             if (courseTimeslot[arr[k+1]]>0){
                                 int TScour = courseTimeslot[arr[k+1]];
                                System.out.println("cek TS setelahnya :"+TScour);
                                     firstSortInitialTS(arr[k], k, conflictcourse, timeslotrooms, suitablerooms,
                                    TScour, suitableslot, afterslot, beforeslot);
                                     System.out.println("sort first");
                                 }   
                             if (courseTimeslot[arr[k]]==0){
                                     firstSortInitialTS(arr[k], k, conflictcourse, timeslotrooms, suitablerooms,
                                    timeslotk, suitableslot, afterslot, beforeslot);
                                     System.out.println("sort first");
                                 }                                   
                            }
                            if (k>0 && k<reqEvent.size()-1){
                                System.out.println("tes tengah");
//                                int corstk = afterslot[arr[k]].length;
//                                System.out.println("lengthnya  :"+ corstk );
//                                if (corstk>0){
//                                int cortk = afterslot[arr[k]][k-1];
//                                System.out.println("course  :"+ cortk );
                                int TScour = courseTimeslot[arr[k-1]];
                                System.out.println("cek TS sebelumnya :"+TScour);
                                if (random > TScour && TScour<timeslot) {
                                    randomInitialTS(arr[k], random, conflictcourse, timeslotrooms, suitablerooms,
                                        timeslot-TScour, suitableslot, afterslot, beforeslot, r, TScour, high);
                                }
                                if (courseTimeslot[arr[k]]==0 && TScour<timeslot){
                                     firstSortInitialTS(arr[k], TScour, conflictcourse, timeslotrooms, suitablerooms,
                                    timeslot-TScour, suitableslot, afterslot, beforeslot);
                                     System.out.println("sort first");
                                 }
                            }
                            if (k==reqEvent.size()-1){
                                System.out.println("tes akhir");
                                int TScour = courseTimeslot[arr[k-1]];
                                System.out.println("cek TS sebelumnya :"+TScour);
                                if (random > TScour) {
                                    randomInitialTS(arr[k], TScour, conflictcourse, timeslotrooms, suitablerooms,
                                    timeslotk-TScour, suitableslot, afterslot, beforeslot, r , low, high);
                                }
                                if (courseTimeslot[arr[k]]==0){
                                    if(beforeslot[arr[k]].length==0){
                                        lastSortInitialTS(arr[k], (timeslot-2), conflictcourse, timeslotrooms, suitablerooms,
                                    timeslotk-TScour, suitableslot, afterslot, beforeslot);
                                        System.out.println("sort last");
                                    }
                                }
                            }
                        }

                    }
                }
        }
        for (int i = 0; i < conflictcourse.length; i++) {
            int low = 1;
            int high = conflictcourse.length+1;
            int index = course.get(i).getIndex();
            int k = -1;
            
//            for (int o = 0; o < conflictcourse.length; o++){
//                System.out.println("index ke "+o+", hitungan timeslotnya"+hitunganTimeslot[o]);
//            }
//            for (int o = 0; o < conflictcourse.length; o++){
//                int random = r.nextInt(high-low) + low;
//                int min = Arrays.stream(hitunganTimeslot).min().getAsInt();
//                
//                if (hitunganTimeslot[o]==min) {
//                    System.out.println("Min = " + min);
//                    System.out.println("indexnya = " + o);
//                    index=o;
//                }
//                course.add(new Course(o, conflictcourse[o].length, 
//                    sizeeventstudent[o], countsuitableroom[o], 
//                    counteventfeatures[o], hitunganTimeslot[o], random));
//                System.out.println("course "+o+", cc length "+conflictcourse[o].length+
//                    ", size student "+sizeeventstudent[o]+", suitable room "+
//                    countsuitableroom[o]+", event features "+counteventfeatures[o]+
//                    ", timeslot "+hitunganTimeslot[o]+", random "+random);
//                }
            if (courseTimeslot[index]==0){
                firstSortInitialTS(index, 0, conflictcourse, timeslotrooms, suitablerooms,
                                    timeslot, suitableslot, afterslot, beforeslot);
            }
        }
        
        for (int i = 0; i < courseTimeslot.length; i++) {
            reqMoveEvent = new ArrayList<>();
            reqSlotOrder = new ArrayList<>();
            int[] nextCourse;
            int max, min=0;
            int tsA=courseTimeslot[i];
            if (tsA>0){
                for (int j = i+1; j < courseTimeslot.length; j++) {
                    int tsB = courseTimeslot[j];
                    if (tsB>0){
                        if (tsA > tsB && suitableorder[i][j]==1) {
                            System.out.println("Event "+i+" di "+tsA + ", Event "+j+" di " +tsB +",suitable order nya <0? "+ suitableorder[i][j]);
                            orderTS++;
                            reqMoveEvent.add(0,i);
                            reqMoveEvent.add(1,j);
                            if (afterslot[j].length>0 || beforeslot[j].length>0){
                                for (int k = 0; k < beforeslot[j].length; k++) {
                                    reqMoveEvent.add(0, beforeslot[j][k]);
                                    }
                                for (int k = 0; k < afterslot[j].length; k++) {
                                    reqMoveEvent.add(reqMoveEvent.size(), afterslot[j][k]);
                                    }
                            }
                            System.out.println("reqMoveEvent "+reqMoveEvent);
//                            for (int k = 0; k < reqMoveEvent.size(); k++) {
//                                int[] arrCekMoveSlot = reqMoveEvent.stream().mapToInt(a -> a).toArray();
////                                    if (arrCekMoveSlot[k]==j){
//////                                        nextCourse = arrCekMoveSlot[k];
////                                    }
//                                    int tsC = courseTimeslot[arrCekMoveSlot[k]];
//                                    reqSlotOrder.add(tsC);
//                            }
//                            System.out.println("reqSlotOrder "+reqSlotOrder);
//                            max = Collections.max(reqSlotOrder);
//                            System.out.println("min slot : "+tsA+", max slot: "+max);
                            moveTimeslotRooms(j, timeslotrooms, courseTimeslot[j], courseRoom[j]);
                            firstSortInitialTS(j, tsA-1, conflictcourse, timeslotrooms, suitablerooms,
                                    timeslot-tsA, suitableslot, afterslot, beforeslot);
                            System.out.println("event "+j+", pindah ke "+courseTimeslot[j]);
                            if (afterslot[j].length>0){
                                for (int k = 0; k < afterslot[j].length; k++) {
                                    reqSlotOrder.add(0, afterslot[j][k]);
                                    }
                                System.out.println("reqSlotOrder "+reqSlotOrder);
                            }
                            int[] arrCekSlot = reqSlotOrder.stream().mapToInt(a -> a).toArray();
                            if (courseTimeslot[j]==0){                            
                                for (int k = 0; k < arrCekSlot.length; k++) {
                                    if (afterslot[arrCekSlot[k]].length == 0){
                                        moveTimeslotRooms(arrCekSlot[k], timeslotrooms, courseTimeslot[arrCekSlot[k]], courseRoom[arrCekSlot[k]]);
                                    }
                                    else {
                                        for (int l = 0; l < afterslot[arrCekSlot[k]].length; l++) {
                                             reqSlotOrder.add(reqSlotOrder.size(), afterslot[arrCekSlot[k]][l]);
                                        }
                                        outerloop:
                                        for (int l = 0; l < reqSlotOrder.size(); l++) {
                                            moveTimeslotRooms(arrCekSlot[l], timeslotrooms, courseTimeslot[arrCekSlot[l]], courseRoom[arrCekSlot[l]]);
                                            break outerloop;
                                        }
                                    }
                                }
                            }
                            else {
                                for (int k = 0; k < arrCekSlot.length; k++) {
                                    if (afterslot[arrCekSlot[k]].length == 0){
                                        moveTimeslotRooms(arrCekSlot[k], timeslotrooms, courseTimeslot[arrCekSlot[k]], courseRoom[arrCekSlot[k]]);
                                        firstSortInitialTS(arrCekSlot[k], courseTimeslot[j], conflictcourse, timeslotrooms, suitablerooms,
                                    (timeslot-courseTimeslot[j]), suitableslot, afterslot, beforeslot);
                                    }
                                }
                            }
                        }
                        
                        if (tsA < tsB && suitableorder[i][j]==-1) {
                            System.out.println("Event "+i+" di "+tsA + ", Event "+j+" di " +tsB +",suitable order nya >0? "+suitableorder[i][j]);
                            orderTS++;
                            reqMoveEvent.add(0,j);
                            reqMoveEvent.add(1,i);
                            if (afterslot[i].length>0 || beforeslot[i].length>0){
                                for (int k = 0; k < beforeslot[i].length; k++) {
                                    reqMoveEvent.add(0, beforeslot[i][k]);
                                    }
                                for (int k = 0; k < afterslot[i].length; k++) {
                                    reqMoveEvent.add(reqMoveEvent.size(), afterslot[i][k]);
                                    }
                            }
                            System.out.println("reqMoveEvent "+reqMoveEvent);
                            for (int k = 0; k < reqMoveEvent.size(); k++) {
                                int[] arrCekMoveSlot = reqMoveEvent.stream().mapToInt(a -> a).toArray();
                                    int tsC = courseTimeslot[arrCekMoveSlot[k]];
                                    reqSlotOrder.add(tsC);
                            }
                            System.out.println("reqSlotOrder "+reqSlotOrder);
                            min = Collections.min(reqSlotOrder);
                            max = Collections.max(reqSlotOrder);
                            System.out.println("min slot : "+min+", max slot: "+max);
//                            if (min == tsA) {
                            moveTimeslotRooms(i, timeslotrooms, courseTimeslot[i], courseRoom[i]);
                            firstSortInitialTS(i, tsB-1, conflictcourse, timeslotrooms, suitablerooms,
                                    timeslot-tsB, suitableslot, afterslot, beforeslot);
                            System.out.println("event "+i+", pindah ke "+courseTimeslot[i]);
                            if (afterslot[i].length>0){
                                for (int k = 0; k < afterslot[i].length; k++) {
                                    reqSlotOrder.add(0, afterslot[i][k]);
                                    }
                                System.out.println("reqSlotOrder "+reqSlotOrder);
                            }
                            int[] arrCekSlot = reqSlotOrder.stream().mapToInt(a -> a).toArray();
                            if (courseTimeslot[i]==0){                            
                                for (int k = 0; k < arrCekSlot.length; k++) {
                                    if (afterslot[arrCekSlot[k]].length == 0){
                                        moveTimeslotRooms(arrCekSlot[k], timeslotrooms, courseTimeslot[arrCekSlot[k]], courseRoom[arrCekSlot[k]]);
                                    }
                                    else {
                                        for (int l = 0; l < afterslot[arrCekSlot[k]].length; l++) {
                                             reqSlotOrder.add(reqSlotOrder.size(), afterslot[arrCekSlot[k]][l]);
                                        }
                                        outerloop:
                                        for (int l = 0; l < reqSlotOrder.size(); l++) {
                                            moveTimeslotRooms(arrCekSlot[l], timeslotrooms, courseTimeslot[arrCekSlot[l]], courseRoom[arrCekSlot[l]]);
                                            break outerloop;
                                        }
                                    }
                                }
                            }
                            else {
                                for (int k = 0; k < arrCekSlot.length; k++) {
                                    if (afterslot[arrCekSlot[k]].length == 0){
                                        moveTimeslotRooms(arrCekSlot[k], timeslotrooms, courseTimeslot[arrCekSlot[k]], courseRoom[arrCekSlot[k]]);
                                        firstSortInitialTS(arrCekSlot[k], courseTimeslot[i], conflictcourse, timeslotrooms, suitablerooms,
                                    (timeslot-courseTimeslot[i]), suitableslot, afterslot, beforeslot);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        } while(orderTS!=0);
    }
    
    void firstSortInitialTS (int index, int currentSlot, int[][] conflictcourse, int[][] timeslotrooms, int[][] suitablerooms,
            int timeslot, int[][] suitableslot, int[][] afterslot, int[][] beforeslot){
        outerloop:  
        for (int i =0; i < timeslot; i++){
            if (searchTS(index, (currentSlot+1), conflictcourse, suitableslot, afterslot, beforeslot)) { //kalau searchTS benar maka
                for (int k = 0; k < timeslotrooms[currentSlot].length; k++) {
                    //cek contraint mengenai timeslot dan ruangan
                    if (cekTimeslotRooms(index, timeslotrooms, suitablerooms, currentSlot, k)){
                        //apabila lolos cek constrain, taruh timeslot dan ruangan
                        placeTimeslotRooms(index, timeslotrooms, currentSlot, k);
//                                System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
//                              if (orderTS (index, j+1, tr, sr, courseTimeslot, suitableorder)) {
//                                hitunganTimeslot[index]=1000000000;
//                                System.out.println("index "+index+"hitungan Timeslotnya "+hitunganTimeslot[index]);
//                        for (int n = 0; n < conflictcourse[index].length; n++){
//                            int updAvailTimeslot =  conflictcourse[index][n];
////                                    hitunganTimeslot[updAvailTimeslot]--;
////                                    course.clear();
//                        }
                        break outerloop;
                    }
                }
            }
            currentSlot++;
        }
    }
    
    void randomInitialTS (int index, int currentSlot, int[][] conflictcourse, int[][] timeslotrooms, int[][] suitablerooms,
            int timeslot, int[][] suitableslot, int[][] afterslot, int[][] beforeslot, Random r, int low, int high){
        outerloop:  
        for (int i =0; i < 100; i++){
            if (searchTS(index, (currentSlot+1), conflictcourse, suitableslot, afterslot, beforeslot)) { //kalau searchTS benar maka
                for (int k = 0; k < timeslotrooms[currentSlot].length; k++) {
                    //cek contraint mengenai timeslot dan ruangan
                    if (cekTimeslotRooms(index, timeslotrooms, suitablerooms, currentSlot, k)){
                        //apabila lolos cek constrain, taruh timeslot dan ruangan
                        placeTimeslotRooms(index, timeslotrooms, currentSlot, k);
//                                System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
//                              if (orderTS (index, j+1, tr, sr, courseTimeslot, suitableorder)) {
//                                hitunganTimeslot[index]=1000000000;
//                                System.out.println("index "+index+"hitungan Timeslotnya "+hitunganTimeslot[index]);
                        for (int n = 0; n < conflictcourse[index].length; n++){
                            int updAvailTimeslot =  conflictcourse[index][n];
//                                    hitunganTimeslot[updAvailTimeslot]--;
//                                    course.clear();
                        }
                        break outerloop;
                    }
                }
            }
            currentSlot= (r.nextInt(high-low) + low);
        }
    }
    
    void lastSortInitialTS (int index, int currentSlot, int[][] conflictcourse, int[][] timeslotrooms, int[][] suitablerooms,
            int timeslot, int[][] suitableslot, int[][] afterslot, int[][] beforeslot){
        outerloop:  
        for (int i = currentSlot; i > 0 ; i--){
            if (searchTS(index, (i+1), conflictcourse, suitableslot, afterslot, beforeslot)) { //kalau searchTS benar maka
                for (int k = 0; k < timeslotrooms[i].length; k++) {
                    //cek contraint mengenai timeslot dan ruangan
                    if (cekTimeslotRooms(index, timeslotrooms, suitablerooms, i, k)){
                        //apabila lolos cek constrain, taruh timeslot dan ruangan
                        placeTimeslotRooms(index, timeslotrooms, i, k);
//                                System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
//                              if (orderTS (index, j+1, tr, sr, courseTimeslot, suitableorder)) {
//                                hitunganTimeslot[index]=1000000000;
//                                System.out.println("index "+index+"hitungan Timeslotnya "+hitunganTimeslot[index]);
                        for (int n = 0; n < conflictcourse[index].length; n++){
                            int updAvailTimeslot =  conflictcourse[index][n];
//                                    hitunganTimeslot[updAvailTimeslot]--;
//                                    course.clear();
                        }
                        break outerloop;
                    }
                }
            }
        }
    }
    
    //if eksplore untuk menghindarkan bentrok
    boolean searchTS(int index, int currentSlot, int[][] conflictcourse, 
            int[][] suitableslot, int[][] afterslot, int[][] beforeslot) {
        for (int i = 0; i < conflictcourse[index].length; i++) {
            int numCourse = conflictcourse[index][i];
            int slotAccept = suitableslot[index][currentSlot-1];
//                System.out.println("currentSlot "+ currentSlot);
                if (slotAccept !=1) {
//                    System.out.println("false");
//                    System.out.println("index ke "+ index);
////            System.out.println("numcoursenya adalah "+numCourse);
//            System.out.println("currentslot ke "+currentSlot);
////            System.out.println("courseTimeslot "+courseTimeslot[numCourse]);
//            System.out.println("slotAccept "+slotAccept);
                    return false;
                }
                else if (slotAccept !=0){
                    
                    if (courseTimeslot[numCourse] == currentSlot) {
//                        System.out.println("false");
//                        System.out.println("index ke "+ index);
////            System.out.println("numcoursenya adalah "+numCourse);
//            System.out.println("currentslot ke "+currentSlot);
////            System.out.println("courseTimeslot "+courseTimeslot[numCourse]);
//            System.out.println("slotAccept "+slotAccept);
                        return false;
                    }
//                    if (afterslot[index].length>0){
//                        for (int j =0; j < afterslot[index].length; j++) {
//                            int afterCourse = afterslot[index][j];
//                            int cekAfterCourseTS = courseTimeslot[afterCourse];
//                            if (cekAfterCourseTS>0){
//                                if (currentSlot < cekAfterCourseTS) return false;
//                            }
//                        }
//                    }
//                    if (beforeslot[index].length>0){
//                        for (int j =0; j < beforeslot[index].length; j++) {
//                            int beforeCourse = beforeslot[index][j];
//                            int cekBeforeCourseTS = courseTimeslot[beforeCourse];
//                            if (cekBeforeCourseTS>0){
//                                if (currentSlot > cekBeforeCourseTS) return false;
//                            }
//                        }
//                    }
//                    else return true;
                    
                }
                                    
        }
        return true;
    }
    
    boolean cekTimeslotRooms(int index, int[][] timeslotrooms, int[][] suitablerooms, int j, int k){
            int tr = timeslotrooms[j][k];
            int sr = suitablerooms[index][k];
            if (tr == 1 && sr == 0) {
                return false;
            }
            if (tr == 0 && sr == 0) {
                return false;
            }
            if (tr == 1 && sr == 1) {
                return false;
            }
        return true;
    }
    
    void placeTimeslotRooms(int index, int[][] timeslotrooms, int j, int k){
        System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
        courseTimeslot[index] = j+1;
        courseRoom[index] = k+1;
//      System.out.println("taruh courseRoom  " +courseRoom[index]);
        timeslotrooms[j][k]++;
    }
    
    void moveTimeslotRooms(int index, int[][] timeslotrooms, int j, int k){
//        System.out.println("Event "+ index +" dipindah dari courseTimeslot " + j);
        if (j!=0){
            j = j-1;
            k = k-1;
        }
        courseTimeslot[index] = 0;
        courseRoom[index] = 0;
//      System.out.println("taruh courseRoom  " +courseRoom[index]);
        timeslotrooms[j][k]--;
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
        System.out.println(/*"distFeasibility"+*/distFeasibility);
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
    
    int precedenceViolation(int[][] suitableorder){
        precedenceViolation=0; 
        for (int i = 0; i < courseTimeslot.length; i++) {
            int tsA=courseTimeslot[i];
            if (tsA>0){
                for (int j = i+1; j < courseTimeslot.length; j++) {
                    int tsB = courseTimeslot[j];
                    if (tsB>0){
                        if (tsA > tsB && suitableorder[i][j]==1) {
//                            System.out.println("Event "+i+" di "+tsA + ", Event "+j+" di " +tsB +",suitable order nya <0? "+ suitableorder[i][j]);
                            precedenceViolation++;
                        }
                        if (tsA < tsB && suitableorder[i][j]==-1) {
//                            System.out.println("Event "+i+" di "+tsA + ", Event "+j+" di " +tsB +",suitable order nya >0? "+suitableorder[i][j]);
                            precedenceViolation++;
                        }
                    }
                }
            }
        }
//        System.out.println("precedenceViolation "+precedenceViolation);
        return precedenceViolation;
    }
}
