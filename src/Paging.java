import java.util.ArrayList;

public class Paging {
	//Data structures for tracking and modifying frames
	private static ArrayList<Frame> frame_table = new ArrayList<>();
    private static int num_references;
    private static int quantum = 3;
    private static String algorithm_type;
    
    public static void main(String [] args) {
    	
    	//As defined in the assignment, seven arguments must be specified
    	if(args.length < 7) {
            System.out.println("Error: Please specify seven arguements");
            return;
        }

    	//Parses input values provided and stores them in respective variables
        int machine_size = Integer.valueOf(args[0]);//M
        int page_size = Integer.valueOf(args[1]);	//P
        int process_size = Integer.valueOf(args[2]);//S
        int job_mix = Integer.valueOf(args[3]);		//J
        num_references = Integer.valueOf(args[4]);	//N
        algorithm_type = args[5];					//R

        //Housekeeping & quick calculations for number of frames & pages
        Process.size = process_size;
        int num_frames = machine_size / page_size;
        int num_pages = Process.size / page_size;

        //Establishes required number of frames
        for(int i = 0; i < num_frames; i++) {
            Frame.frames.add(new Frame(i));
        }

        //Formats and prints provided input values
        System.out.println("The machine size is " + machine_size + ".");
        System.out.println("The page size is " + page_size + ".");
        System.out.println("The process size is " + Process.size + ".");
        System.out.println("The job mix number is " + job_mix + ".");
        System.out.println("The number of references per process is " + num_references + ".");
        System.out.println("The replacement algorithm is " + algorithm_type + ".");
        System.out.println("The level of debugging output is " + args[6] + ".\n");

        //Four possible sets of processes as specified
        switch (job_mix) {
	        case 1:
	            Process.processes.add(new Process(1, 1, 0, 0));
	            break;
	        case 2:
	        	Process.processes.add(new Process(1, 1, 0, 0));
	            Process.processes.add(new Process(2, 1, 0, 0));
	            Process.processes.add(new Process(3, 1, 0, 0));
	            Process.processes.add(new Process(4, 1, 0, 0));
	            break;
	        case 3:
	        	Process.processes.add(new Process(1, 0, 0, 0));
	            Process.processes.add(new Process(2, 0, 0, 0));
	            Process.processes.add(new Process(3, 0, 0, 0));
	            Process.processes.add(new Process(4, 0, 0, 0));
	            break;
	        case 4:
	            Process.processes.add(new Process(1, .75, .25, 0));
	            Process.processes.add(new Process(2, .75, 0, .25));
	            Process.processes.add(new Process(3, .75, .125, .125));
	            Process.processes.add(new Process(4, .5, .125, .125));
	            break;
        }

        //Now that we have our processes added based on job mix, we must assign an upper and lower bound for each process
        for(Process cur_process : Process.processes) {
            for(int i = 0; i < num_pages; i++) {
                int lower_bound = page_size * i;
                int upper_bound = (lower_bound + page_size) - 1;
                cur_process.pages.add(new Page(lower_bound, upper_bound, cur_process));
            }
        }
        
        for(int i = Frame.frames.size() -1; i >= 0; i--) {
            frame_table.add(Frame.frames.get(i));
        }
        //Check for processes still active
        while(!is_complete()) {            
        	for(Process process : Process.processes) {
                for(int i = 0; i < quantum; i++) {
                    if(process.num_references == num_references) {
                        break;
                    }                   
                    Page cur_page = process.get_page();
                    //If page frame doesn't not exist, evict based on specified algo                                  
                    if(cur_page.frame == null) {
                        cur_page.frame = evict();
                        cur_page.frame.page = cur_page;
                        process.fault_count++;
                    }                    
                    else if(algorithm_type.equals("lru")) {                   	
                    	frame_table.remove(cur_page.frame);
                    	frame_table.add(cur_page.frame);
                    }                   
                    Process.incr_residency();
                    process.num_references++;
                    process.next_word();
                }
            }
        }
        print_details();
    }

    private static Frame evict() {
        Frame next = null;
    	switch (algorithm_type) {
            case "lru":
            	next = frame_table.remove(0);
                if(next.page != null) {
                    next.evict();
                }
                frame_table.add(next);
                return next;
            
            case "lifo":
            	next = null;
                for(int i = frame_table.size() - 1; i >= 0; i--) {
                    if(frame_table.get(i).page == null) {
                        next = frame_table.remove(i);
                        break;
                    }
                }
                if(next == null) {
                    next = frame_table.remove(frame_table.size() - 1);
                }
                if(next.page != null) {
                    next.evict();
                }
                frame_table.add(next);
                return next;
            
            case "random":
                for(Frame frame : frame_table) {
                    if(frame.page == null) {
                        next = frame;
                        break;
                    }
                }
                if(next == null) {
                    int frame_id = RandomGen.nextInt() % frame_table.size();
                    for (Frame cur_frame : frame_table) {
                        if (cur_frame.frame_id == frame_id) {
                            next = cur_frame;
                        }
                    }
                }
                if(next.page != null) {
                    next.evict();
                }
                return next;            
            default:
            	return next;
        }
    }

    private static boolean is_complete() {
        for(Process cur_process : Process.processes) {
            if(cur_process.num_references < num_references)
                return false;
        }
        return true;
    }

    private static void print_details() {
    	int fault_num = 0;
        double residency_count = 0;
        int evicted_count = 0;
        
        for(Process proc : Process.processes) {
            fault_num += proc.fault_count;
            System.out.print("Process " + proc.process_id + " had " + proc.fault_count + " fault(s)");
            if(proc.evicted_count == 0) {
                System.out.println("\n\tWith no evictions, the average residence is undefined.");
            }
            else {
                double avgResidency = (double)proc.residency / proc.evicted_count;
                residency_count += proc.residency;
                evicted_count += proc.evicted_count;
                System.out.println(" and " + avgResidency + " average residency.");
            }
        }
        System.out.print("\nThe total number of faults is " + fault_num);
        if(evicted_count == 0) {
            System.out.println("\n\tWith no evictions, the overall average residence is undefined.");
        }
        else {
            double totalAvgResidency = residency_count / evicted_count;
            System.out.println(" and the overall average residency is " + totalAvgResidency + ".");
        }
    }
}
