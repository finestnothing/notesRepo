package CSc139.hw04;

public class RRScheduler {
    private Job[] jobs = new Job[0];
    private int timePassed = 0;
    private int quantum;
    private int overhead;
    private int numJobs;
    private int totalTATime = 0;;
/*

Need to handle RR overhead

*/
    public RRScheduler(int quantum, int overhead){
        this.quantum = quantum;
        this.overhead = overhead;
    }

    public int getOverhead(){
        return this.overhead;
    }


    public void addJob(Job job) {
        Job[] newJobs = new Job[jobs.length + 1];
        for (int i = 0; i < jobs.length; i++) {
            newJobs[i] = jobs[i];
        }
        newJobs[jobs.length] = job;
        jobs = newJobs;
    }
    
    public void run(){
        this.numJobs = jobs.length;
        boolean updated = false;
        do{
            updated = false;
            for (Job i : jobs){
                if(i.getTime() > quantum){
                    Job newSlice = new Job(i.getId(), i.getTime() - quantum, i.getPriority());
                    addJob(newSlice);
                    i.setTime(quantum);
                    i.setNextSlice(newSlice);
                    updated = true;
                }
            }
        }while(updated);

        for(Job i : jobs){
            // Set job delay to timePassed
            i.setDelay(timePassed);
            // Add time for job to timePassed
            timePassed += i.getTime() + overhead;
        }
    }

    public void print(){
        System.out.printf("Preemptive RR Process Execution, quantum = %d, overhead = %d%n", quantum, overhead);
        
        for(int i = 0; i < numJobs; i ++){
            Job tempJob = jobs[i];
            int totalTime = tempJob.getTime() + tempJob.getDelay();
            int totalSlices = 1;
            while(tempJob.nextSlice != null){
                tempJob = tempJob.nextSlice;
                totalTime += tempJob.getTime() + tempJob.getDelay();
                totalSlices++;
            }
            totalTATime = totalTime;
            System.out.printf("RR TA time for finished p%d = %d, and: %d time slices%n", tempJob.getId(), totalTime, totalSlices);
        }
        
        System.out.printf("RR Throughput with quantum %d, overhead %d is %.5f p/ms%n", quantum, overhead, (double)numJobs/timePassed);
        System.out.printf("Average RR TA with quantum %d, overhead %d is %.5f%n", quantum, overhead, (double)totalTATime/numJobs);
        System.out.println("<><> end RR <><>%n");
    }
}
