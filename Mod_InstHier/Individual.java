package Mod_InstHier;

import java.util.Comparator;

public class Individual implements Comparable<Individual> {
	//Evolving traits----------------------------------------------
    // Social strategy (Intrinsic influence)
    private double s;
    public void mutateS() {if(s==0) {s=1;}else {s=0;}}
    // Preference for institution
    private int h;
    public void mutateH() {if(h==0) {h=1;}else {h=0;}}
    //Other traits------------------------------------------------
    //Realized influence
    private double alpha;
    // Preference
    private double x_init;
    // Preference Negotiated
    private double x;
    //Number of negotiations
    private double count_nego;
    //fitness
    private double w;
    //Number of offsprings
    private int off;
    
    
    public static int nbreIndividual = 0;
    public static int getNbreIndividual(){
        return nbreIndividual;
    }
    
    public Individual(double s, int h, double x_init){
    	this.s = s;
    	this.h = h;
    	this.alpha = -1;
    	this.x_init = 0; 
    	this.x = 0;
    	this.count_nego = 0;
    	this.w = -1;
    	this.off = -1;
    	nbreIndividual++;
    }
    
    public Individual(Individual pIndividual){
        s = pIndividual.s;
        h = pIndividual.h;
        alpha = -1;
        x_init = 0;
        x = 0;
        count_nego = 0;
        w = -1;
        off = -1;
        nbreIndividual++;
    }
    //Setters
    public void setS(double s){this.s = s;}
    public void setH(int h) {this.h = h;}
    public void setAlpha(double alpha){this.alpha = alpha;}
    //Set initial opinion also reset nego opinion
    public void setXInit(double x_init){this.x_init = x_init;this.x=x_init;}
    public void setX(double x){this.x = x;}
    public void setW(double w){this.w = w;}
    public void setOff(int off){this.off = off;}
    //Getters
    public double getS(){return s;}
    public double getH() {return h;}
    public double getAlpha(){return alpha;}
    public double getXInit(){return x_init;}
    public double getX(){return x;}
    public double getCountNego(){return count_nego;}
    public double getW(){return w;}
    public int getOff(){return off;}
    
    public void addCountNego(){
        this.count_nego++;
    }
    
    @Override
    public int compareTo(Individual obj)
    {
        // compareTo returns a negative number if this is less than obj, 
        // a positive number if this is greater than obj, 
        // and 0 if they are equal.
        if(this.alpha < obj.alpha)
          return 1;
        else if(obj.alpha < this.alpha)
          return -1;
          return 0;
    }
    

	
    
   
}


