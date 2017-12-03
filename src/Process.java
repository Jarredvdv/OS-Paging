import java.util.ArrayList;

class Process {
	ArrayList<Page> pages;
    static ArrayList<Process> processes = new ArrayList<>();
	
    //Job mix specifications
	int process_id;
	double a;//A
    double b;//B
    double c;//C
    int address;
    int num_references;
    int residency;
    
    int fault_count = 0;
    int evicted_count = 0;
    static int size = 0;
        
    //Constructor for process objects
    Process(int process_id, double a, double b, double c) {
    	pages = new ArrayList<>();
    	this.process_id = process_id;
    	
        this.a = a;
        this.b = b;
        this.c = c;
        
        residency = 0;
        num_references = 0;
        address = (111 * process_id) % size;
    }
	
    //Returns page that exists within specified bounds
    Page get_page() {
        for(Page cur_page : pages) {
            if(address >= cur_page.lower_bound && address <= cur_page.upper_bound) {
            	return cur_page;
            }
        }
        return null;
    }
    
    void set_page(Page page) {
        for(Page cur_page : pages) {
            if(address <= cur_page.upper_bound && address >= cur_page.lower_bound) {
            	page = cur_page;
            }
        }    
    }
    
    //Updates the next reference based on current reference 
    public void next_word() {    
        double result = RandomGen.nextInt()/(Integer.MAX_VALUE + 1d);
        if(result < a) {
        	address = ((address + 1) + size) % size;
        } else if(result < a+b) {
            address = ((address - 5) + size) % size;
        } else if(result < a+b+c) {    
        	address = ((address + 4) + size) % size;
        } else {
            address = (RandomGen.nextInt() + size) % size;
        }
    }
    
    //Increases residencies for all pages
    static void incr_residency() {
        for(Process process : processes) {
            for(Page cur_page : process.pages) {
                if(cur_page.frame != null) {                   
                	cur_page.residency++;
                }
            }
        }
    }
}
