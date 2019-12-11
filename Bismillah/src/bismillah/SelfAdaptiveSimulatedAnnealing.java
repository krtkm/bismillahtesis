/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bismillah;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.*;
/**
 *
 * @author tika
 */
public class SelfAdaptiveSimulatedAnnealing {
    InitialSolution initialSolution;
    Random r = new Random();
    int high, currentPenalty, newPenalty, M, randomLH,
            randomEvent1, randomEvent2, randomEvent3, randomEvent4,
            randomSlot1, randomSlot2, randomSlot3, newDistFeasibility;
    boolean hardConstraint;
    double p, delta, deltaDistFeasibility;
    int[] currentTimeslot, currentRoom, newTimeslot, newRoom, LHscore, NL;
    int[][] schStudent;
    CheckHC checkHC;
    CheckPenalti checkPenalti;
    WriteSol writeSol;
    ReadSol readSol = new ReadSol();
    int maxrand = 4; //jumlah LLH diganti di sini
    int minrand = 1; 
    int range = maxrand - minrand + 1;
    int totalNL = 8; //jumlah NL diganti di sini
    int distFeasibility;
    
    SelfAdaptiveSimulatedAnnealing(String sourceFile, int[][]mStudentEvent, int[][] suitableRoom, 
            int[][] suitableSlot, int[][] suitableOrder, int[][] conflictCourse, int[][] beforeSlot, int[][] afterSlot,
            int timeslot, double T, double Tstop, double alpa, /*int tabulistLength,*/ long startTime, int timelimit,
            int[] initialTS, int[] initialRoom, String exp, /*int tabuLLH,*/ int Tchange, 
            int Nreheating, double beta,
            int[] sizeeventstudent, int[] countsuitableroom, int[] counteventfeatures, int[] countorderslot, int[][] timeslotrooms) 
            throws IOException {
        M = 1000000;
        checkHC = new CheckHC(); 
        checkPenalti = new CheckPenalti();
        distFeasibility = checkPenalti.distFeasibility(initialTS, initialRoom, sizeeventstudent, beforeSlot, afterSlot);
        int n = 1;
        
        LHscore = new int[2];
        for (int i = 0; i < LHscore.length; i++) {
            LHscore[i] = 50;
        }
        
        //index NL Self-Adaptive [BARU]
        NL = new int[totalNL];
        // generate random numbers within 1 to 10 
        for (int k=0; k< totalNL; k++) { 
            int rand = (int)(Math.random() * range) + minrand; 
            // Output is different everytime this code is executed 
            NL[k]=rand;
//            System.out.println("isi array " + rand);
        }
        
        //WNL Self-Adaptive [BARU]
        LinkedList WNL = new LinkedList();
        
        readInitialSol(mStudentEvent, suitableRoom, 
                suitableSlot, suitableOrder,
                timeslot, initialTS, initialRoom);
        high = currentTimeslot.length;
        

        do {
            //menghilangkan nilai WNL
            WNL.clear();
            for (int k=0; k< totalNL; k++) { 
                
                switch(NL[k]) {
                  case 1: newTimeslot = move1Ts(currentTimeslot, timeslot); break;
                  case 2: newTimeslot = swap2Ts(currentTimeslot); break;
                  case 3: newTimeslot = move2Ts(currentTimeslot, timeslot); break;
                  case 4: newTimeslot = swap3Ts(currentTimeslot); break;
//                case 5: newTimeslot = move3Ts(currentTimeslot, timeslot); break;
//                case 6: newTimeslot = swap4Ts(currentTimeslot); break;
//                default: newTimeslot = currentTimeslot.clone(); break;
                }
            newRoom = currentRoom.clone();
            for (int j=0; j<conflictCourse.length; j++){
                if (currentTimeslot[j]==0){
                    System.out.println(j);
                    firstSortOptimizationTS(j, 0, conflictCourse, timeslotrooms, suitableRoom,
                                timeslot, suitableSlot, afterSlot, beforeSlot);
                }
            }
            int[][] schStudent = readSol.studentAvail(mStudentEvent, timeslot, newTimeslot);
            hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, 
                    suitableSlot, suitableOrder,
                    newRoom, newTimeslot);
            if (hardConstraint) {
//                for (int j=0; j<conflictCourse.length; j++){
//                    if (currentTimeslot[j]==0 && beforeSlot[j].length==0 && afterSlot[j].length==0){
//                        System.out.println(j);
//                        firstSortOptimizationTS(j, 0, conflictCourse, timeslotrooms, suitableRoom,
//                                    timeslot, suitableSlot, afterSlot, beforeSlot);
//                    }
//                }
                newPenalty = checkPenalti.totalPenalti(schStudent);
                newDistFeasibility = checkPenalti.distFeasibility(newTimeslot, newRoom, sizeeventstudent, beforeSlot, afterSlot);
                System.out.println("newDistFeasibility " +newDistFeasibility);
            } else {
                newPenalty = M;
            }
            delta = currentPenalty - newPenalty;
            deltaDistFeasibility = distFeasibility - newDistFeasibility;
//            System.out.println("Current Penalty Score: " + currentPenalty);
//            System.out.println("New Penalty Score: " + newPenalty);
            if (delta > 0 && deltaDistFeasibility >= 0 && newPenalty != M) {
                currentTimeslot = newTimeslot.clone();
                currentRoom = newRoom.clone();
                currentPenalty = newPenalty;
                distFeasibility = newDistFeasibility;
                System.out.println("Accept Solution");
                //add to WNL
                WNL.add(NL[k]);
//                outerloop:
//            } else if (deltaDistFeasibility >= 0){
//                p = Math.pow(Math.E, (-(Math.abs(delta)/T)));
////                System.out.println("delta " +delta);
////                System.out.println("T " +T);
////                System.out.println("hitungan " +(-(Math.abs(delta)/T)));
////                System.out.println("boltzman " +p);
////                System.out.println("R " +printR);
//                if (p > r.nextDouble()) {
////                    currentTimeslot = newTimeslot.clone();
//                    currentRoom = newRoom.clone();
//                    currentPenalty = newPenalty;
////                    System.out.println("Accept Solution");
//                    //add to WNL
//                    WNL.add(NL[k]);
//                                for (int j=0; j<conflictCourse.length; j++){
//                if (initialTS[j]==0){
//                    if (beforeSlot.length==0 && afterSlot.length==0){
//                        System.out.println("nyobain");
//                        initialSolution = new InitialSolution(conflictCourse, sizeeventstudent, countsuitableroom, counteventfeatures
//            , timeslot, countorderslot);
//                        initialSolution.firstSortInitialTS(j, timeslot, conflictCourse, suitableRoom, suitableRoom, timeslot, suitableSlot, afterSlot, beforeSlot);
//                    }
//                }
//            }
//                          
//                }          
            }
            if (n%Tchange == 0) {
                T = T * alpa;
//                System.out.println("suhu alpa "+T);
            }
            if (n%Nreheating == 0) {
                T = T + (T*beta);
//                System.out.println("suhu beta"+T);
            }
            n++;

            }
            
            
            Arrays.fill(NL, 0);
//            //coba print reset NL
////            String strArray[] = new String[WNLsize];
////            for (int i = 0; i < WNLsize; i++) {
////                strArray[i] = String.valueOf(WNL[i]);
////                    System.out.println("List WNL " + strArray[i]);
////            }
//
////            System.out.println("List WNL " + WNL);
            int WNLsize= WNL.size();
            maxrand = WNLsize;
//            System.out.println("size WNL "+WNL.size());
            //75% element NL
            int NLbaru = Math.abs((3*totalNL)/4);
//            System.out.println("NLbaru " + NLbaru);
            for (int j=0; j<= totalNL-1; j++) { 
                //apabila WNL kosong, NL dirandom lagi
                if (WNLsize == 0){
                    int rand = (int)(Math.random() * range) + minrand;
                    NL[j]=rand;
                }
                //75% dari NL diisi dengan WNL
                else if(NLbaru > j){
                    if (WNLsize > j) {
                        NL[j]=(int)WNL.get(j);
//                        System.out.println("lolos " + WNL.get(j));
                    }
//                    else if (j % (WNLsize)==0) {
//                        NL[j]=(int)WNL.get(0);
//                    }
                    else{
                        int ulangWNL = (int)Math.random()*WNLsize;
                        NL[j]=(int)WNL.get(ulangWNL);
//                      System.out.println("cek "+  NL[j]);
                    }
                }
                //25% dari NL diisi random
                else{
                    int rand = (int)(Math.random() * range) + minrand;
                    NL[j]=rand;
                    }
            
            }
        } while(currentPenalty>0 && System.currentTimeMillis()-startTime<timelimit);
                //n<=iteration
        writeSol = new WriteSol(sourceFile.split(".tim")[0] + "exp" + exp +".sol", currentTimeslot, currentRoom);
        System.out.println("Jumlah Iterasi : " + n);
    }
    
    //move mindah ke slot yang kosong / random. kalo ada isinya, tetep dituker tp ga lolos hard constraints
    final int[] move1Ts(int[] currenttimeslot, int timeslot) {
        int[] newTS = currenttimeslot.clone();
        randomEvent1 = r.nextInt(high);
        do {
            randomSlot1 = r.nextInt(timeslot-1) + 1;
        } while (randomSlot1 == newTS[randomEvent1]);
        newTS[randomEvent1] = randomSlot1;
        return newTS;
    }
    
    final int[] move2Ts(int[] currenttimeslot, int timeslot) {
        int[] newTS = currenttimeslot.clone();
        randomEvent1 = r.nextInt(high);
        randomEvent2 = r.nextInt(high);
        do {
            randomSlot1 = r.nextInt(timeslot-1) + 1;
            randomSlot2 = r.nextInt(timeslot-1) + 1;
        } while (randomSlot1 == newTS[randomEvent1] || 
                randomSlot2 == newTS[randomEvent2]);
        newTS[randomEvent1] = randomSlot1;
        newTS[randomEvent2] = randomSlot2;
        return newTS;
    }
    
//    final int[] move3Ts(int[] currenttimeslot, int timeslot) {
//        int[] newTS = currenttimeslot.clone();
//        randomEvent1 = r.nextInt(high);
//        randomEvent2 = r.nextInt(high);
//        randomEvent3 = r.nextInt(high);
//        do {
//            randomSlot1 = r.nextInt(timeslot-1) + 1;
//            randomSlot2 = r.nextInt(timeslot-1) + 1;
//            randomSlot3 = r.nextInt(timeslot-1) + 1;
//        } while (randomSlot1 == newTS[randomEvent1] || 
//                randomSlot2 == newTS[randomEvent2] ||
//                randomSlot3 == newTS[randomEvent3]);
//        newTS[randomEvent1] = randomSlot1;
//        newTS[randomEvent2] = randomSlot2;
//        newTS[randomEvent3] = randomSlot3;
//        return newTS;
//    }
    
    //swap memindah slot
    final int[] swap2Ts(int[]currenttimeslot) {
        int [] newTS = currenttimeslot.clone();
        do {
            randomEvent1 = r.nextInt(high);
            randomEvent2 = r.nextInt(high);
        } while (randomEvent1 == randomEvent2);
        int temp = newTS[randomEvent1];
        newTS[randomEvent1] = newTS[randomEvent2];
        newTS[randomEvent2] = temp;
        return newTS;
    }
    
    final int[] swap3Ts(int[]currenttimeslot) {
        int [] newTS = currenttimeslot.clone();
        do {
            randomEvent1 = r.nextInt(high);
            randomEvent2 = r.nextInt(high);
            randomEvent3 = r.nextInt(high);
        } while (randomEvent1 == randomEvent2 || randomEvent2 == randomEvent3);
        int temp1 = newTS[randomEvent1];
        int temp2 = newTS[randomEvent2];
        newTS[randomEvent1] = newTS[randomEvent3];
        newTS[randomEvent2] = temp1;
        newTS[randomEvent3] = temp2;
        return newTS;
    }
    
//    final int[] swap4Ts(int[]currenttimeslot) {
//        int [] newTS = currenttimeslot.clone();
//        do {
//            randomEvent1 = r.nextInt(high);
//            randomEvent2 = r.nextInt(high);
//            randomEvent3 = r.nextInt(high);
//            randomEvent4 = r.nextInt(high);
//        } while (randomEvent1 == randomEvent2 ||
//                randomEvent2 == randomEvent3 ||
//                randomEvent3 == randomEvent4);
//        int temp1 = newTS[randomEvent1];
//        int temp2 = newTS[randomEvent2];
//        int temp3 = newTS[randomEvent3];
//        newTS[randomEvent1] = newTS[randomEvent4];
//        newTS[randomEvent2] = temp1;
//        newTS[randomEvent3] = temp2;
//        newTS[randomEvent4] = temp3;
//        return newTS;
//    }
    
    void readInitialSol(int[][]mStudentEvent, int[][] suitableRoom, 
            int[][] suitableSlot, int[][] suitableOrder,
            int timeslot, int[] initialTS, int[] initialRooms) {
        currentTimeslot = initialTS.clone();
        currentRoom = initialRooms.clone();
        schStudent = readSol.studentAvail(mStudentEvent, timeslot, currentTimeslot);
        
        hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, 
                suitableSlot, suitableOrder, 
                currentRoom, currentTimeslot);
        if (hardConstraint) {
            currentPenalty = checkPenalti.totalPenalti(schStudent);
        } else {
            currentPenalty = M;
        }
        System.out.println(/*"Initial Solution : "*/ + currentPenalty);
    }
    
    double[] rouletteWheele(int[] LHscore) {
        double totalscore = IntStream.of(LHscore).sum();
        double[] probability = new double[LHscore.length];
        for (int i = 0; i < LHscore.length; i++) {
            if (i==0) {
                probability[i] = (double) LHscore[i]/totalscore;
            } else {
                probability[i] = (double) (LHscore[i-1]+LHscore[i])/totalscore;
            }
        }
        return probability;
    }
    
    int indexLargestArray(int[] LHscore) {
        int max = LHscore[0];
        int index = 0;
        for (int i = 0; i < LHscore.length; i++) {
            if (max < LHscore[i]) {
                max = LHscore[i];
                index = i;
            }
        }
        return index;
    }
    
    void firstSortOptimizationTS (int index, int currentSlot, int[][] conflictcourse, int[][] timeslotrooms, int[][] suitablerooms,
            int timeslot, int[][] suitableslot, int[][] afterslot, int[][] beforeslot){
        outerloop:  
        for (int i =0; i < timeslot; i++){
            if (searchTS(index, (currentSlot+1), conflictcourse, suitableslot, beforeslot, afterslot)) { //kalau searchTS benar maka
                for (int k = 0; k < timeslotrooms[currentSlot].length; k++) {
                    //cek contraint mengenai timeslot dan ruangan
                    if (cekTimeslotRooms(index, timeslotrooms, suitablerooms, currentSlot, k)){
                        //apabila lolos cek constrain, taruh timeslot dan ruangan
                        placeTimeslotRooms(index, timeslotrooms, currentSlot, k);
//                       System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
                        break outerloop;
                    }
                }
            }
            currentSlot++;
        }
    }
    
    void randomOptimizationTS (int index, int currentSlot, int[][] conflictcourse, int[][] timeslotrooms, int[][] suitablerooms,
        int timeslot, int[][] suitableslot, int[][] afterslot, int[][] beforeslot, Random r, int low, int high){
    outerloop:  
        for (int i =0; i < 100; i++){
            if (searchTS(index, (currentSlot+1), conflictcourse, suitableslot, beforeslot, afterslot)) { //kalau searchTS benar maka
                for (int k = 0; k < timeslotrooms[currentSlot].length; k++) {
                    //cek contraint mengenai timeslot dan ruangan
                    if (cekTimeslotRooms(index, timeslotrooms, suitablerooms, currentSlot, k)){
                        //apabila lolos cek constrain, taruh timeslot dan ruangan
                        placeTimeslotRooms(index, timeslotrooms, currentSlot, k);
    //                                System.out.println("Event "+ index +" ditaruh courseTimeslot " + (j+1));
                        break outerloop;
                    }
                }
            }
            currentSlot= (r.nextInt(high-low) + low);
        }
    }
        
        boolean searchTS(int index, int currentSlot, int[][] conflictcourse, 
            int[][] suitableslot, int[][] beforeslot, int[][] afterslot) {
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
                    
                    if (newTimeslot[numCourse] == currentSlot || currentTimeslot [numCourse] == currentSlot) {
//                        System.out.println("false");
//                        System.out.println("index ke "+ index);
////            System.out.println("numcoursenya adalah "+numCourse);
//            System.out.println("currentslot ke "+currentSlot);
////            System.out.println("courseTimeslot "+courseTimeslot[numCourse]);
//            System.out.println("slotAccept "+slotAccept);
                        return false;
                    }
                    if (afterslot[index].length>0){
                        for (int j =0; j < afterslot[index].length; j++) {
                            int afterCourse = afterslot[index][j];
                            int cekCourseTS = newTimeslot[afterCourse];
                            int cekAfterCourseTS = currentTimeslot[afterCourse];
                            if (cekAfterCourseTS>0){
                                if (currentSlot > cekAfterCourseTS) return false;
                            }
                            if (cekCourseTS>0){
                                if (currentSlot > cekCourseTS) return false;
                            }
                        }
                    }
                    if (beforeslot[index].length>0){
                        for (int j =0; j < beforeslot[index].length; j++) {
                            int beforeCourse = beforeslot[index][j];
                            int cekBeforeCourseTS = currentTimeslot[beforeCourse];
                            int cekCourseTS = newTimeslot[beforeCourse];
                            if (cekBeforeCourseTS>0){
                                if (currentSlot < cekBeforeCourseTS) return false;
                            }
                            if (cekCourseTS>0){
                                if (currentSlot < cekCourseTS) return false;
                            }
                        }
                    }
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
        currentTimeslot[index] = j+1;
        currentRoom[index] = k+1;
        newTimeslot[index] = j+1;
        newRoom[index] = k+1;
//      System.out.println("taruh courseRoom  " +courseRoom[index]);
        timeslotrooms[j][k]++;
    }
    
}
