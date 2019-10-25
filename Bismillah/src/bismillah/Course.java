package bismillah;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author AM
 */
public class Course {

    private int index;
    private int noConflict;
    private int noStudent;
    private int noRooms;
    private int noFeatures;
    private int randomRank;
    
    public Course(int index, int noConflict, int noStudent, int noRooms, int noFeatures, int randomRank) {
        this.index = index;
        this.noConflict = noConflict;
        this.noStudent = noStudent;
        this.noRooms = noRooms;
        this.noFeatures = noFeatures;
        this.randomRank = randomRank;
    }
    
    /**
     * @return the noRooms
     */
    public int getNoRooms() {
        return noRooms;
    }

    /**
     * @param noRooms the noRooms to set
     */
    public void setNoRooms(int noRooms) {
        this.noRooms = noRooms;
    }
    
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the noConflict
     */
    public int getNoConflict() {
        return noConflict;
    }

    /**
     * @param noConflict the noConflict to set
     */
    public void setNoConflict(int noConflict) {
        this.noConflict = noConflict;
    }

    /**
     * @return the noStudent
     */
    public int getNoStudent() {
        return noStudent;
    }

    /**
     * @param noStudent the noStudent to set
     */
    public void setNoStudent(int noStudent) {
        this.noStudent = noStudent;
    }
    
        /**
     * @return the noFeatures
     */
    public int getNoFeatures() {
        return noFeatures;
    }

    /**
     * @param noFeatures the noFeatures to set
     */
    public void setNoFeatures(int noFeatures) {
        this.noFeatures = noFeatures;
    }

    /**
     * @return the randomRank
     */
    public int getRandomRank() {
        return randomRank;
    }

    /**
     * @param randomRank the randomRank to set
     */
    public void setRandomRank(int randomRank) {
        this.randomRank = randomRank;
    }
}

class courseChained implements Comparator<Course> {
    private List<Comparator<Course>> listComparators;
    
    @SafeVarargs
    public courseChained(Comparator<Course>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }
    
    @Override
    public int compare(Course o1, Course o2) {
        for (Comparator<Course> comparator : listComparators) {
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
    
}

class courseSortingConflict implements Comparator<Course>{
    @Override
    public int compare(Course o1, Course o2) {
        return o2.getNoConflict() - o1.getNoConflict();
    }   
}

class courseSortingRooms implements Comparator<Course>{
    @Override
    public int compare(Course o1, Course o2) {
        return o1.getNoRooms()- o2.getNoRooms();
    }   
}

class courseSortingRandom implements Comparator<Course>{
    @Override
    public int compare(Course o1, Course o2) {
        return o1.getRandomRank()- o2.getRandomRank();
    }   
}

class courseSortingFeatures implements Comparator<Course>{
    @Override
    public int compare(Course o1, Course o2) {
        return o2.getNoFeatures()- o1.getNoFeatures();
    }   
}

class courseSortingStudent implements Comparator<Course>{
    @Override
    public int compare(Course o1, Course o2) {
        return o2.getNoStudent()- o1.getNoStudent();
    }   
}
