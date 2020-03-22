package Mod_InstHier;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import com.beust.jcommander.Parameter;

public class Parameters {
	  @Parameter
	  public List<String> parameters = new ArrayList<>();
	  
	  //Simulation
	  @Parameter(names = {"-id"}, description = "ID of process on cluster")
	  public Long process_id = 0L;
	  @Parameter(names = {"-sP"}, description = "step for printing data in gen")
	  public Integer step_print;
	  @Parameter(names = {"-NS"}, description = "Number of simulations")
	  public Integer N_simul;
	  @Parameter(names = {"-de"}, description = "Details of output: 0 gen 1 patch 2 ind")
	  public Integer detail;
	  @Parameter(names = {"-fP"}, description = "First generation from which we output")
	  public Integer first_print;
	  
	  //Evolution processes
	  @Parameter(names = {"-NG" }, description = "Number of generations")
	  public Integer N_gen;
	  @Parameter(names = {"-mu" }, description = "Mutation rate")
	  public Double mu;
	  //@Parameter(names = {"-sigma" }, description = "strength of the mutations")
	  //public Double sigma;
	  
	  //Ecological parameters
	  @Parameter(names = {"-NP" }, description = "Number of patches")
	  public Integer N_patch;
	  @Parameter(names = {"-NIni" }, description = "Initial size of population in one patch")
	  public Integer N_ind_init;
	  @Parameter(names = {"-K" }, description = "Carrying capacity")
	  public Integer K;
	  
	  //Life history traits
	  @Parameter(names = {"-rI" }, description = "Intrinsic growth rate of one individual")
	  public Double r_i;
	  @Parameter(names = {"-rMax" }, description = "Maximum additional growth rate")
	  public Double r_max;
	  @Parameter(names = {"-rInc" }, description = "Steepness of the increase of growth rate")
	  public Double r_inc;
	  @Parameter(names = {"-m" }, description = "Migration rate")
	  public Double m;
	  //@Parameter(names = {"-CM" }, description = "Cost of migration rate = probability to die during migration")
	  //public Double C_mig;

	  //Individual trait
	  @Parameter(names = {"-aL" }, description = "Influence of an individual with leader strategy")
	  public Double alpha_l;
	  @Parameter(names = {"-aF" }, description = "Influence of an individual with follower strategy")
	  public Double alpha_f;
	  
	  
	  //Collective action and additional resources
	  @Parameter(names = {"-BMax" }, description = "Maximum ressources produced by collective action")
	  public Double B_max;
	  @Parameter(names = {"-BMid" }, description = "Population size which produced half of the maximum possible collective ressources")
	  public Double B_mid;
	  @Parameter(names = {"-BInc" }, description = "Steepness of the increase of collective ressources")
	  public Double B_inc;
	  //@Parameter(names = {"--lambda","-L" }, description = "Amount of ressources remaining at the next generation")
	  //public Double lambda;

	  //Collective decision-making process
	  @Parameter(names = {"-NL" }, description = "Number of listeners during a single negotiation event")
	  public Integer N_l;
	  @Parameter(names = {"-xThr" }, description = "Variance threshold under which consensus is considered reached")
	  public Double x_thr;
	  @Parameter(names = {"-CN" }, description = "Cost of a single negotiaiton event")
	  public Double C_nego;

	  //Collective institutions
	  @Parameter(names = {"-CH" }, description = "Cost of institution")
	  public Double C_h;
	  @Parameter(names = {"-CT" }, description = "Inverse of Total cost of collective decision making")
	  public Double C_t_star;

	  //Others
	  @Parameter(names = {"-d" }, description = "ecological inequality")
	  public Double d;
	  

	  
	  


      
}
