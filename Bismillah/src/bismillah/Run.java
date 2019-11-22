package bismillah;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author AM
 */
public class Run {

    public static void main(String[] args) throws IOException {
        
//        String[] fileName = {"small1.tim", "small2.tim", "small3.tim", "small4.tim", "small5.tim", 
//            "medium1.tim", "medium2.tim", "medium3.tim", "medium4.tim", "medium5.tim", "large.tim"};
        String[] fileName = {"early1.tim", "early2.tim", "early3.tim", "early4.tim", "early5.tim",
            "early6.tim", "early7.tim", "early8.tim", "late9.tim", "late10.tim", "late11.tim",
            "late12.tim", "late13.tim", "late14.tim", "late15.tim", "late16.tim", "hidden17.tim",
            "hidden18.tim", "hidden19.tim", "hidden20.tim", "hidden21.tim", "hidden22.tim",
            "hidden23.tim", "hidden24.tim"};
//        String sourceFile = fileName[(Integer.parseInt(args[0]))-1];
        int instance = 24;
        String sourceFile = fileName[instance-1];
//        String sourceFile = "small1.tim";
//        for (int i = 1; i < 12; i++) {
//        for (int i = Integer.parseInt(args[1].split("-")[0]); i <= Integer.parseInt(args[1].split("-")[1]); i++) {
        String exp = String.valueOf(1);

        //For T-SA
            double T = 95;
            double Tstop = 0;
            double alpa = 0.999;
            double beta = 0.5;
            int tabulistLength = 3;
            int Tchange = 50;
            int Nreheating = 25000;
            //int tabuLLH = 4;
        
//        //For NLGD
//            int problemCategory, min, max, Bmin, Bmax;
//            double decayrate;
//            double beta = 0;

//        int iteration = Integer.parseInt(args[1]);
        
        InitialSolution initialSolution;
        int[][] conflictMatrix, conflictCourse, timeslotRoom, suitableRoom, suitableSlot, mStudentEvent,
                suitableOrder, beforeSlot, afterSlot;
        int[] sizeStudentEvent, countSuitableRoom,countEventFeature;
        int noTS, timeslot, timeLimit = 0, iteration = 0, distFeasibility;
        boolean hardConstraint;
        
        //timelimit
        if (instance-1 < 30) {
            timeLimit = 90000;
//            iteration = 3000000;
        } 
//        else if (instance-1 < 10) {
//            timeLimit = 900000;
////            iteration = 6000000;
//        } 
//        else {
//            timeLimit = 9000000;
////            iteration = 20000000;
//        }
        
        System.out.println("Source File : " + sourceFile);
        long startTime = System.currentTimeMillis();
        ReadFile readFile = new ReadFile(sourceFile);
        conflictMatrix = readFile.conflictMatrix();
        conflictCourse = readFile.conflictCourse();
        timeslotRoom = readFile.timeslotRoom();
        suitableRoom = readFile.mSuitableRoom;
        suitableSlot = readFile.suitableSlot();
        suitableOrder = readFile.suitableOrder();
        beforeSlot = readFile.beforeSlot();
        afterSlot = readFile.afterSlot();
        mStudentEvent = readFile.mStudentEvent;
        sizeStudentEvent = readFile.sizeStudentEvent;
        countSuitableRoom = readFile.countSuitableRoom;
        countEventFeature = readFile.countEventFeature;
        timeslot = readFile.timeslot;
        do {
            initialSolution = new InitialSolution(conflictCourse, sizeStudentEvent, countSuitableRoom, countEventFeature
            , afterSlot, timeslot);
            initialSolution.exploreSlot(conflictCourse, sizeStudentEvent, countSuitableRoom, 
                    countEventFeature, timeslot, timeslotRoom, 
                    suitableRoom, suitableSlot, suitableOrder, beforeSlot, afterSlot);

            initialSolution.noTimeslot(sizeStudentEvent);
            noTS = initialSolution.noTimeslot.size();
            distFeasibility = initialSolution.distFeasibility;
        } while(noTS!=0);
        
        int[] courseTimeslot = initialSolution.courseTimeslot;
        int[] courseRoom = initialSolution.courseRoom;
        
        TabuSimulatedAnnealing tsa = new TabuSimulatedAnnealing(sourceFile, 
                mStudentEvent, suitableRoom, 
                suitableSlot, suitableOrder,
                timeslot, T, Tstop, alpa, tabulistLength, startTime, timeLimit, 
                courseTimeslot, courseRoom, exp, /*tabuLLH,*/ Tchange, Nreheating, beta);
        
////        SelfAdaptiveSimulatedAnnealing sasa = new SelfAdaptiveSimulatedAnnealing(sourceFile, 
////                mStudentEvent, suitableRoom, timeslot, 
////                T, Tstop, alpa, /*tabulistLength,*/ startTime, timeLimit, 
////                courseTimeslot, courseRoom, exp, /*tabuLLH,*/ Tchange, Nreheating, beta);
        
        //Verify Constraint
        CheckHC checkHC = new CheckHC();
        CheckPenalti checkPenalti = new CheckPenalti();
        ReadSol readSol = new ReadSol(sourceFile.split(".tim")[0] + "exp" + exp +".sol");
        int[] currentT = readSol.solTimeslot;
        int[] currentR = readSol.solRoom;
        int[][] schStudent = readSol.studentAvail(mStudentEvent, timeslot, currentT);
        hardConstraint = checkHC.hardConstraint(schStudent, suitableRoom, 
                suitableSlot, suitableOrder,
                currentR, currentT);
        if (hardConstraint) {
            System.out.println("Solusi Akhir : " + checkPenalti.totalPenalti(schStudent));
        }
        
        }
}
