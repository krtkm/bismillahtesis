/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bismillah;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.*;
/**
 *
 * @author tika
 */
public class SelfAdaptiveSimulatedAnnealing {
    Random r = new Random();
    int high, currentPenalty, newPenalty, M, randomLH,
            randomEvent1, randomEvent2, randomEvent3, randomEvent4,
            randomSlot1, randomSlot2, randomSlot3, cobahehe;
    boolean hardConstraint;
    double p, delta;
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
    
    SelfAdaptiveSimulatedAnnealing(String sourceFile, int[][]mStudentEvent, int[][] suitableRoom, int timeslot, 
            double T, double Tstop, double alpa, /*int tabulistLength,*/ long startTime, int timelimit,
            int[] initialTS, int[] initialRoom, String exp, /*int tabuLLH,*/ int Tchange, 
            int Nreheating, double beta) throws IOException {
        M = 1000000;
        LinkedList<int[]> tabu = new LinkedList<>();
//        LinkedList<Integer> LLH = new LinkedList<>();
        checkHC = new CheckHC(); 
        checkPenalti = new CheckPenalti();
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
            System.out.println("isi array " + rand);
        }
        
        //WNL Self-Adaptive [BARU]
        LinkedList WNL = new LinkedList();
        
        readInitialSol(mStudentEvent, suitableRoom, timeslot, initialTS, initialRoom);
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
            int[][] schStudent = readSol.studentAvail(mStudentEvent, timeslot, newTimeslot);
            hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, newRoom, newTimeslot);
            if (hardConstraint) {
                newPenalty = checkPenalti.totalPenalti(schStudent);
            } else {
                newPenalty = M;
            }
            delta = currentPenalty - newPenalty;
//            System.out.println(newPenalty);
//            
//            System.out.println("Current Penalty Score: " + currentPenalty);
//            System.out.println("New Penalty Score: " + newPenalty);
            if (delta > 0 && newPenalty != M) {
                currentTimeslot = newTimeslot.clone();
                currentRoom = newRoom.clone();
                currentPenalty = newPenalty;
                System.out.println("Accept Solution");
                //add to WNL
                WNL.add(NL[k]);
            } else {
                p = Math.pow(Math.E, (-(Math.abs(delta)/T)));
//                System.out.println("delta " +delta);
//                System.out.println("T " +T);
//                System.out.println("hitungan " +(-(Math.abs(delta)/T)));
//                System.out.println("boltzman " +p);
                double printR = r.nextDouble();
//                System.out.println("R " +printR);
                if (p > printR) {
//                    currentTimeslot = newTimeslot.clone();
                    currentRoom = newRoom.clone();
                    currentPenalty = newPenalty;
                    System.out.println("Accept Solution");
                    //add to WNL
                    WNL.add(NL[k]);
                          
                } 
                else {
                    System.out.println("Reject");
                }
                
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
                        System.out.println("lolos " + WNL.get(j));
                    }
//                    else if (j % (WNLsize)==0) {
//                        NL[j]=(int)WNL.get(0);
//                    }
                    else{
                        int ulangWNL = (int)Math.random()*WNLsize;
                        NL[j]=(int)WNL.get(ulangWNL);
                      System.out.println("cek "+  NL[j]);
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
    
    void readInitialSol(int[][]mStudentEvent, int[][] suitableRoom, int timeslot, 
            int[] initialTS, int[] initialRooms) {
        currentTimeslot = initialTS.clone();
        currentRoom = initialRooms.clone();
        schStudent = readSol.studentAvail(mStudentEvent, timeslot, currentTimeslot);
        
        hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, currentRoom, currentTimeslot);
        if (hardConstraint) {
            currentPenalty = checkPenalti.totalPenalti(schStudent);
        } else {
            currentPenalty = M;
        }
        System.out.println("Initial Solution : " + currentPenalty);
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
    
}
