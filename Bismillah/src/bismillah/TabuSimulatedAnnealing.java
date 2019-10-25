package bismillah;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.*;

/**
 *
 * @author user
 */
public class TabuSimulatedAnnealing {
    Random r = new Random();
    int high, currentPenalty, newPenalty, M, randomLH,
            randomEvent1, randomEvent2, randomEvent3, randomEvent4,
            randomSlot1, randomSlot2, randomSlot3;
    boolean hardConstraint;
    double p, delta;
    int[] currentTimeslot, currentRoom, newTimeslot, newRoom, LHscore;
    int[][] schStudent;
    CheckHC checkHC;
    CheckPenalti checkPenalti;
    WriteSol writeSol;
    ReadSol readSol = new ReadSol();
    
    TabuSimulatedAnnealing(String sourceFile, int[][]mStudentEvent, int[][] suitableRoom, /*int[][] suitableSlot,*/
            int timeslot, double T, double Tstop, double alpa, int tabulistLength, long startTime, int timelimit,
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
        
        readInitialSol(mStudentEvent, suitableRoom, /*suitableSlot,*/ timeslot, initialTS, initialRoom);
        high = currentTimeslot.length;
        
        
        do {
            //roulette wheel probability, random
            double[] rw = rouletteWheele(LHscore);
            double mathRandom = Math.random();
            if (mathRandom < rw[0]) {
                newTimeslot = move1Ts(currentTimeslot, timeslot);
                randomLH = 0;
            } else {
                newTimeslot = swap2Ts(currentTimeslot);
                randomLH = 1;
//            } else if(mathRandom < rw[2]) {
//                newTimeslot = move2Ts(currentTimeslot, timeslot);
//                randomLH = 2;
//            } else if(mathRandom < rw[3]) {
//                newTimeslot = swap3Ts(currentTimeslot);
//                randomLH = 3;
//            } else if(mathRandom < rw[4]) {
//                newTimeslot = move3Ts(currentTimeslot, timeslot);
//                randomLH = 4;
//            } else {
//                newTimeslot = swap4Ts(currentTimeslot);
//                randomLH = 5;
            }
            
            //roulette wheel probability, W/O random
//            int probability = indexLargestArray(LHscore);
//            switch(probability) {
//                case 0: newTimeslot = move1Ts(currentTimeslot, timeslot); break;
//                case 1: newTimeslot = swap2Ts(currentTimeslot); break;
////                case 2: newTimeslot = move2Ts(currentTimeslot, timeslot); break;
////                case 3: newTimeslot = swap3Ts(currentTimeslot); break;
////                case 4: newTimeslot = move3Ts(currentTimeslot, timeslot); break;
////                case 5: newTimeslot = swap4Ts(currentTimeslot); break;
//                default: newTimeslot = currentTimeslot.clone(); break;
//            }
            
            //random LH biasa
////            do {
//                randomLH = (r.nextInt(LHlength))+1;
////            } while(LLH.contains(randomLH));
//            switch(randomLH) {
//                case 1: newTimeslot = move1Ts(currentTimeslot, timeslot); break;
//                case 2: newTimeslot = swap2Ts(currentTimeslot); break;
////                case 3: newTimeslot = move2Ts(currentTimeslot, timeslot); break;
////                case 4: newTimeslot = swap3Ts(currentTimeslot); break;
////                case 5: newTimeslot = move3Ts(currentTimeslot, timeslot); break;
////                case 6: newTimeslot = swap4Ts(currentTimeslot); break;
//                default: newTimeslot = currentTimeslot.clone(); break;
//            }
            newRoom = currentRoom.clone();
            int[][] schStudent = readSol.studentAvail(mStudentEvent, timeslot, newTimeslot);
            hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, /*suitableSlot,*/ newRoom, newTimeslot);
            if (hardConstraint) {
                newPenalty = checkPenalti.totalPenalti(schStudent);
            } else {
                newPenalty = M;
            }
            delta = currentPenalty - newPenalty;
            
//            System.out.println("Current Penalty Score: " + currentPenalty);
//            System.out.println("New Penalty Score: " + newPenalty);
            if (delta > 0 && newPenalty != M) {
                currentTimeslot = newTimeslot.clone();
                currentRoom = newRoom.clone();
                currentPenalty = newPenalty;
                if (LHscore[randomLH] < 100) {
                     LHscore[randomLH] = LHscore[randomLH]+10;
                }
//                System.out.println("Accept Solution");
            } else {
                p = Math.pow(Math.E, (-(Math.abs(delta)/T)));
                if (p > r.nextDouble()) {
                    if (tabu.contains(newTimeslot)) {
                        if (tabu.size() >= tabulistLength) {
                            tabu.pop();
                            tabu.push(newTimeslot);
                        } else {
                            tabu.push(newTimeslot);
                        }
//                        if (LLH.size() >= tabuLLH) {
//                            LLH.pop();
//                            LLH.push(randomLH);
//                        } else {
//                            LLH.push(randomLH);
//                        }
                        if (LHscore[randomLH] > 5) {
                            LHscore[randomLH] = LHscore[randomLH]-5;
                        }
//                        System.out.println("Reject Solution");
                    } else {
                        currentTimeslot = newTimeslot.clone();
                        currentRoom = newRoom.clone();
                        currentPenalty = newPenalty;
                        if (LHscore[randomLH] < 100) {
                            LHscore[randomLH] = LHscore[randomLH]+10;
                        }
                        if (tabu.size() >= tabulistLength) {
                            tabu.pop();
                            tabu.push(currentTimeslot);
                        } else {
                            tabu.push(currentTimeslot);
                        }
//                        System.out.println("Accept Solution");
                    }
//                } else {
//                    if (LLH.size() >= tabuLLH) {
//                        LLH.pop();
//                        LLH.push(randomLH);
//                    } else {
//                        LLH.push(randomLH);
//                    }
                } else {
                    if (LHscore[randomLH] > 5) {
                        LHscore[randomLH] = LHscore[randomLH]-5;
                    }
//                    System.out.println("Reject Solution");
                }
                
            }
            if (n%Tchange == 0) {
                T = T * alpa;
            }
            if (n%Nreheating == 0) {
                T = T + (T*beta);
            }
            n++;
//            System.out.println();
        } while(currentPenalty>0 && System.currentTimeMillis()-startTime<timelimit);
                //n<=iteration
        writeSol = new WriteSol(sourceFile.split(".tim")[0] + "exp" + exp +".sol", currentTimeslot, currentRoom);
        System.out.println("Jumlah Iterasi : " + n);
    }
    
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
    
    final int[] move3Ts(int[] currenttimeslot, int timeslot) {
        int[] newTS = currenttimeslot.clone();
        randomEvent1 = r.nextInt(high);
        randomEvent2 = r.nextInt(high);
        randomEvent3 = r.nextInt(high);
        do {
            randomSlot1 = r.nextInt(timeslot-1) + 1;
            randomSlot2 = r.nextInt(timeslot-1) + 1;
            randomSlot3 = r.nextInt(timeslot-1) + 1;
        } while (randomSlot1 == newTS[randomEvent1] || 
                randomSlot2 == newTS[randomEvent2] ||
                randomSlot3 == newTS[randomEvent3]);
        newTS[randomEvent1] = randomSlot1;
        newTS[randomEvent2] = randomSlot2;
        newTS[randomEvent3] = randomSlot3;
        return newTS;
    }
    
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
    
    final int[] swap4Ts(int[]currenttimeslot) {
        int [] newTS = currenttimeslot.clone();
        do {
            randomEvent1 = r.nextInt(high);
            randomEvent2 = r.nextInt(high);
            randomEvent3 = r.nextInt(high);
            randomEvent4 = r.nextInt(high);
        } while (randomEvent1 == randomEvent2 ||
                randomEvent2 == randomEvent3 ||
                randomEvent3 == randomEvent4);
        int temp1 = newTS[randomEvent1];
        int temp2 = newTS[randomEvent2];
        int temp3 = newTS[randomEvent3];
        newTS[randomEvent1] = newTS[randomEvent4];
        newTS[randomEvent2] = temp1;
        newTS[randomEvent3] = temp2;
        newTS[randomEvent4] = temp3;
        return newTS;
    }
    
    void readInitialSol(int[][] mStudentEvent, int[][] suitableRoom, int timeslot, int[] initialTS, int[] initialRooms) {
        currentTimeslot = initialTS.clone();
        currentRoom = initialRooms.clone();
        schStudent = readSol.studentAvail(mStudentEvent, timeslot, currentTimeslot);
        
        hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, /*suitableSlot,*/ currentRoom, currentTimeslot);
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
