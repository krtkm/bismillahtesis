package bismillah;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.*;

/**
 *
 * @author user
 */
public class NLGreatDeluge {
    Random r = new Random();
    int high, currentPenalty, newPenalty, M, randomLH,
            randomEvent1, randomEvent2, randomEvent3, randomEvent4, randomEvent5,
            randomSlot1, randomSlot2, randomSlot3, randomSlot4, randomSlot5;
    boolean hardConstraint;
    double p, delta, decayrate;
    int level, range;
    int[] currentTimeslot, currentRoom, newTimeslot, newRoom, LHscore;
    int[][] schStudent;
    CheckHC checkHC;
    CheckPenalti checkPenalti;
    WriteSol writeSol;
    ReadSol readSol = new ReadSol();
    
    NLGreatDeluge(String sourceFile, int[][]mStudentEvent, int[][] suitableRoom, int timeslot, 
            int[] initialTS, int[] initialRoom, long startTime, int timeLimit,
            String exp, int problemCategory, double decayrate, int min, int max,
            int Bmin, int Bmax, double beta) throws IOException {
        M = 1000000;
        LinkedList<int[]> tabu = new LinkedList<>();
        checkHC = new CheckHC(); 
        checkPenalti = new CheckPenalti();
        int n = 1;
        
        LHscore = new int[6];
        for (int i = 0; i < LHscore.length; i++) {
            LHscore[i] = 50;
        }
        
        readInitialSol(mStudentEvent, suitableRoom, timeslot, initialTS, initialRoom);
        high = currentTimeslot.length;
        
        level = currentPenalty;
        
        do {
            //roulette wheel probability, random
            double[] rw = rouletteWheels(LHscore);
            double mathRandom = Math.random();
            if (mathRandom < rw[0]) {
                newTimeslot = move1Ts(currentTimeslot, timeslot);
                randomLH = 0;
            } else if (mathRandom < rw[1]) {
                newTimeslot = swap2Ts(currentTimeslot);
                randomLH = 1;
            } else if (mathRandom < rw[2]) {
                newTimeslot = move2Ts(currentTimeslot, timeslot);
                randomLH = 2;
            } else if (mathRandom < rw[3]) {
                newTimeslot = swap3Ts(currentTimeslot);
                randomLH = 3;
            } else if (mathRandom < rw[4]) {
                newTimeslot = move3Ts(currentTimeslot, timeslot);
                randomLH = 4;
            } else {
                newTimeslot = swap4Ts(currentTimeslot);
                randomLH = 5;
            }
            
            newRoom = currentRoom.clone();
            int[][] schStudent = readSol.studentAvail(mStudentEvent, timeslot, newTimeslot);
            hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, newRoom, newTimeslot);
            if (hardConstraint) {
                newPenalty = checkPenalti.totalPenalti(schStudent);
            } else {
                newPenalty = M;
            }
            
            if ((newPenalty < currentPenalty || newPenalty < level) && 
                    (newPenalty != M)) {
                currentTimeslot = newTimeslot.clone();
                currentRoom = newRoom.clone();
                currentPenalty = newPenalty;
                if (LHscore[randomLH] < 100) {
                    LHscore[randomLH] = LHscore[randomLH]+10;
                }
            } else {
                if (LHscore[randomLH] > 5) {
                    LHscore[randomLH] = LHscore[randomLH]-5;
                }
            }
            range = level - newPenalty;
            if ((range < 1 || level <= currentPenalty) && newPenalty != M) {
                if (problemCategory == 3 || problemCategory == 1) {
                    level += (Bmin + r.nextInt((Bmax - Bmin)+1));
                } else {
                    if (currentPenalty < newPenalty) {
                        level += (Bmin + r.nextInt((Bmax - Bmin)+1));
                    } else {
                        level += 25;
                    }
                }
            } else {
                level *= (Math.pow(Math.E, 
                        (-decayrate*(min + r.nextInt((max - min)+1))))) + beta;
            }
            n++;
        } while(currentPenalty>0 && System.currentTimeMillis()-startTime<timeLimit);
                //System.currentTimeMillis()-startTime<timeLimit
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
    
    double[] rouletteWheels(int[] LHscore) {
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
