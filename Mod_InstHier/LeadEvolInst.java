package Mod_InstHier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.stat.descriptive.moment.Variance;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

//Made to be run from console
public class LeadEvolInst {
            
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
    	
    	//Initialize and parse parameters from options
    	Parameters parameters = new Parameters();
    	JCommander.newBuilder()
    	  .addObject(parameters)
    	  .build()
    	  .parse(args);
        
        //Working directory
        String wd;
    	// wd = "C:/Users/40011091/Phd-Thèse/A1-Projects/EvolLeadMod/C-Results/Res 4.0/";
        wd = System.getProperty("user.dir")+"/";

        //Name file
        String name_file= "";
        for(int i=3+1; i<args.length+1; i=i+2) {name_file = name_file + args[i-1] + "_" + args[i];}
        //nameFile = nameFile.substring(1) + "-";
        
        //Seed initialization
        long process_id;
        long seed;
        
        System.out.println(name_file);
        //Writer initialization
        File f = new File(wd + args[1] + args[2] + name_file + ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
        

        //Parameters fixed because we don't vary them much (to avoid too long name)
        //NindIni=10,K=20,N_patch=50
        for(int iSimul =0; iSimul < parameters.N_simul; iSimul++){
        new Model(
                out, seed =  parameters.process_id + iSimul, name_file, parameters.step_print, parameters.detail, parameters.first_print,
            	parameters.N_gen, parameters.mu,
            	50, 10, 20,
            	parameters.r_i, parameters.r_max, parameters.r_inc, parameters.m,
            	parameters.alpha_l,parameters.alpha_f,
            	parameters.B_max, parameters.B_mid, parameters.B_inc, 
            	parameters.N_l, parameters.x_thr, parameters.C_nego,
            	parameters.C_h, parameters.C_t_star,
            	parameters.d
            ).run(); 
        };
        

        out.close();
     
       
    }
    
}
