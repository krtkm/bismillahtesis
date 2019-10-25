package bismillah;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author AM
 */
public class InitialSolution {
    ArrayList<Course> course = new ArrayList<>();
    ArrayList<Integer> noTimeslot;
    ArrayList<List<Integer>> conflictTimeslot;
    int[] courseRoom, courseTimeslot;
    int lengthSlot;
    
    InitialSolution(int[][] conflictcourse, int[] sizeeventstudent, 
            int[] countsuitableroom, int[]counteventfeatures) {
        Random r = new Random();
        int low = 1;
        int high = conflictcourse.length+1;
        for (int i = 0; i < conflictcourse.length; i++) {
            int random = r.nextInt(high-low) + low;
            course.add(new Course(i, conflictcourse[i].length, 
                    sizeeventstudent[i], countsuitableroom[i], 
                    counteventfeatures[i], random));                            //menyimpan paket nilai kedalam arraylist
//            System.out.println("course "+course);
        
        }
        Collections.sort(course, new courseChained(                             //sorting object
                new courseSortingRooms(),
                new courseSortingConflict(),
                new courseSortingRandom()
                ));
        courseTimeslot = new int[sizeeventstudent.length];                      //menyimpan timeslot final
        courseRoom = new int[sizeeventstudent.length];                          //menyimpan room final
    }
    
    //Assign jadwal initial solution ke timeslot dan room yang sesuai
    void exploreSlot(int[][] conflictcourse, int timeslot, int[][] timeslotrooms, 
            int[][] suitablerooms, int[][] suitableslot, int[][] suitableorder) {
        for (int i = 0; i < conflictcourse.length; i++) {
            int index = course.get(i).getIndex();
//            System.out.println("index ke " + index);
            outerloop: 
            for (int j = 0; j < timeslot; j++) { //yang di loop harusnya timeslotnya yang bisa berubah
                if (searchTS(index, j+1, conflictcourse, suitableslot)) { //kalau searchTS benar maka
                    for (int k = 0; k < timeslotrooms[j].length; k++) {
                        int tr = timeslotrooms[j][k];
                        int sr = suitablerooms[index][k];
                        int slotskrg=j+1;
//                        if (tr == 0 && sr == 1) {
                        if (orderTS (index, j+1, tr, sr, courseTimeslot, suitableorder)) {    
                            System.out.println("Event "+ index +" ditaruh courseTimeslot " + slotskrg);
                            courseTimeslot[index] = j+1;
                            courseRoom[index] = k+1;
//                            System.out.println("taruh courseRoom  " +courseRoom[index]);
                            timeslotrooms[j][k]++;
                            break outerloop;
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
//            System.out.println("index ke "+index);
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
    
    boolean orderTS (int index, int currentTimeslot, int tr, int sr, 
            int[] courseTimeslot, int [][] suitableorder){
        for (int i = 0; i < courseTimeslot.length; i++) {
            if (courseTimeslot[i]>0 && tr==0 && sr==1){
                int cekSlot = courseTimeslot[i];
                //suitableorder
//                System.out.println("timeslot ke-" + courseTimeslot[i]);
                if (suitableorder[index][i]!=0 ){
                    if (currentTimeslot<cekSlot && suitableorder[index][i]!=1){
                        return false;
                    }
                    else if (currentTimeslot>cekSlot && suitableorder[index][i]==1){
                        return false;
                    }
                }
            }     
        }
        return true;
        
    }
    
    //Cek semua event telah terjadwal
    void noTimeslot() {
        noTimeslot = new ArrayList<>();
        for (int i = 0; i < courseTimeslot.length; i++) {
            if (courseTimeslot[i] < 1 || courseRoom[i] < 1) {
                noTimeslot.add(i);
            }
        }
    }
}
