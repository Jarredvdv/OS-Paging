class Page {
    Process process;
    Frame frame;
    
    int residency = 0;
    int lower_bound;
    int upper_bound;
    
    public Page(int lower_bound, int upper_bound, Process cur_process) {
        this.lower_bound = lower_bound;
        this.upper_bound = upper_bound;
        process = cur_process;
    }
    
}
