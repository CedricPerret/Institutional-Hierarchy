package Mod_InstHier;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.*;
import java.util.zip.*;
import org.apache.commons.math3.stat.descriptive.moment.Variance;


public class Model extends Thread {
	private ZipOutputStream pw; private long seed; private String name_file; private int step_print; private int detail; private int first_print;
	private int N_gen; private double mu;
	private int N_patch; private int N_ind_init; private int K;
	private double r_i; private double r_max; private double r_inc; private double m;
	private double alpha_l; private double alpha_f;
	private double B_max; private double B_mid; private double B_inc;
	private int N_l; private double x_thr; private double C_nego;
	private double C_h; private double C_t_star;
	private double d;

	public Model(ZipOutputStream pw, long seed, String name_file, int step_print, int detail, int first_print,
			int N_gen, double mu,
			int N_patch, int N_ind_init, int K,
			double r_i, double r_max, double r_inc, double m,
			double alpha_l, double alpha_f,
			double B_max, double B_mid, double B_inc,
			int N_l, double x_thr, double C_nego, 
			double C_h, double C_t_star, 
			double d) {
		this.pw = pw; this.seed = seed; this.name_file = name_file; this.step_print = step_print; this.detail = detail; this.first_print = first_print;
		this.N_gen = N_gen; this.mu = mu;
		this.N_patch = N_patch; this.N_ind_init = N_ind_init; this.K = K;
		this.r_i = r_i; this.r_max = r_max; this.r_inc = r_inc; this.m = m;
		this.alpha_l = alpha_l; this.alpha_f = alpha_f;
		this.B_max = B_max; this.B_mid = B_mid; this.B_inc = B_inc;
		this.N_l = N_l; this.x_thr = x_thr; this.C_nego = C_nego;
		this.C_h = C_h; this.C_t_star = C_t_star;
		this.d = d;
	}

	// Majority rule for choice of the institution
	public double leadInstitutionVote(List<Individual> pop) {
		double sum_h = 0;
		for (Individual i : pop) {
			sum_h += i.getH();
		}
		double res = sum_h / pop.size();
		if (res > 0.5) {
			res = 1;
		} else {
			res = 0;
		}
		return (res);
	}

	// @Override
	public void run() {
		//INITIALIZATION ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// Parameters and variable declaration =========================
		double x_var;	//Variance of opinions
		double prob_temp;	//Temporary probability of speaking (before normalization)
		int speaker;	//Index of speaker
		int N_l2;	//Updated number of listeners (<= nL if N<nL)
		double diff_alpha;	//Difference of influence
		int nEvent;	//Number of time step for consensus
		double bTot;	//Total of resources produced
		double bThTot; //Total of resources produced before cost of organisation
		double I;	//Cost of organisation
		double w;	//Fitness
		double rB;	//Additional growth rate (from collective action)
		String res_temp = "";	//String for output
		List<Individual> list_leaders = new ArrayList<>();	//List of potential chief (leader strategy) to pick up from
		double sum_alpha;	//Sum of alpha to normalize
		double sum_distrib;	//Sum of share to normalize

		
		// Table--------------------------------------------------
		// Collective decision
		double[][] res_x_consensus = new double[N_gen + 1][N_patch];
		// Time to reach consensus
		double[][] res_t_consensus = new double[N_gen + 1][N_patch];
		// Total resources produced
		double[][] res_B_tot = new double[N_gen + 1][N_patch];
		// Total resources before cost of organisation
		double[][] res_B_th_tot = new double[N_gen + 1][N_patch];
		// Form of social organisation collectively voted
		double[][] res_h_star = new double[N_gen + 1][N_patch];
		// Total resources produced at previous generations
		double[] B_tot_pre = new double[N_patch];


		// Random generator
		Utility utility = new Utility(new Random(seed));

		// Create Entry inside the zip
		System.out.println(name_file);
		ZipEntry e = new ZipEntry(name_file + "-seed_ " + seed + ".txt");
		try {
			pw.putNextEntry(e);
		} catch (IOException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}

		
		// Initialization =========================================
		// Table for populations
		List<List<Individual>> pop_now = new ArrayList<>();
		List<List<Individual>> pop_next = new ArrayList<>();
		
		for (int j = 0; j < N_patch; j++) {
			pop_now.add(new ArrayList<>());
			pop_next.add(new ArrayList<>());
			// Initial bTotPre Array
			B_tot_pre[j] = 0; 
			// Initial population at t=0
			for (int k = 0; k < N_ind_init; k++) {
				pop_now.get(j).add(new Individual(
						//social strategy
						utility.randomDouble(),
						//h pref for institution
						0,
						//Initial opinion
						utility.randomDouble()));
			}
		}
		
		
		
		// SIMULATIONS+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//Print progress
		//LOOP GEN----------------------------------------
		for (int i_gen = 0; i_gen < N_gen; i_gen++) {
			if ((i_gen % Math.round(N_gen / 10)) == 0) {
				System.out.println("Simul " + seed + " Gen " + (i_gen * 100) / N_gen + "%");
			}
			
			//LOOP PATCH-----------------------------------
			for (int i_patch = 0; i_patch < N_patch; i_patch++) {
				// Empty patch-----------------------------------------------
				// Jump
				if (pop_now.get(i_patch).isEmpty()) {
					continue;
				}

				
				// INSTITUION GAME====================================================================
				// Decision-making for institution 
				// if C_h ==100, ONLY informal hierarchy, institutional hierarchy is not allowed
				// if C_h ==0.001, ONLY institutional hierarchy, informal hierarchy is not allowed
				if(C_h == 100) {res_h_star[i_gen][i_patch] = 0;}
				else if (C_h == 0.001) {res_h_star[i_gen][i_patch] = 1;}
				else {res_h_star[i_gen][i_patch] = this.leadInstitutionVote(pop_now.get(i_patch));}
				
				// Effect of institution
				if(res_h_star[i_gen][i_patch] == 0) {
					for(Individual i_ind: pop_now.get(i_patch)) {
						if(i_ind.getS()==1) {i_ind.setAlpha(alpha_l);}
						else {i_ind.setAlpha(alpha_f);}
					}
				} else if (res_h_star[i_gen][i_patch] == 1) {
					//First set everyone to follower and then pick a chief among leaders strategies
					for(Individual k: pop_now.get(i_patch)) {
						k.setAlpha(alpha_f);
						if(k.getS()==1) {list_leaders.add(k);};
					}
					//If no leaders, just take a random individual
					if(list_leaders.isEmpty()) {list_leaders.add(pop_now.get(i_patch).get(utility.randomIntegerRange(pop_now.get(i_patch).size())));}
					//Pick and set leader
					list_leaders.get(utility.randomIntegerRange(list_leaders.size())).setAlpha(alpha_l);
					list_leaders.clear();
				}else {System.out.println("Unknown value of institution");}

                    
				// Opinion formation==================================================================
				//Initialization=-------------------------------------------------------------------------
				//Set up initial opinion to random
				for(int i_ind = 0; i_ind<pop_now.get(i_patch).size();i_ind++) {
					pop_now.get(i_patch).get(i_ind).setXInit(utility.randomDouble());
				}
			
				x_var = utility.sdOpinions(pop_now.get(i_patch)); // Initial pref sd
				nEvent = 0; // Initial number of negotiation event
				double[] probSpeaker = new double[pop_now.get(i_patch).size()]; // Initial array of the probability of speaking
																			// of each individual
				
                //Probability of speaking (normalised)-------------
				sum_alpha = 0;
				for (int k = 0; k < pop_now.get(i_patch).size(); k++) {
					sum_alpha += Math.pow(pop_now.get(i_patch).get(k).getAlpha(),4);
				}
				prob_temp = 0;
				// We calculate the weigthed probabilities of being a speaker
				for (int k = 0; k < pop_now.get(i_patch).size(); k++) { 
					probSpeaker[k] = prob_temp + (Math.pow(pop_now.get(i_patch).get(k).getAlpha(),4)/ sum_alpha);
					prob_temp = probSpeaker[k];
				}
				// Set size of listeners and set up list
				if (pop_now.get(i_patch).size() <= N_l) {
					N_l2 = (pop_now.get(i_patch).size()) - 1;
				} // To avoid looking for non existing listeners
				else {
					N_l2 = N_l;
				}
				int[] list_listeners = new int[N_l2];
				
				//Simulations of negotiations-----------------------------------------------------------------
				while (x_var > x_thr) {
					// Pick speaker
					speaker = utility.probSample(probSpeaker, utility.randomDouble()); 
					//Count number of negotiation
					pop_now.get(i_patch).get(speaker).addCountNego(); 
					//Choose randomly listeners
					list_listeners = utility.randomSampleOtherList(pop_now.get(i_patch).size(), N_l2, speaker); 
					// Update opinion listener
					for (int l = 0; l < N_l2; l++) {
						diff_alpha = pop_now.get(i_patch).get(speaker).getAlpha()- pop_now.get(i_patch).get(list_listeners[l]).getAlpha();
						// If values of alpha are the same
						if (diff_alpha <= 0.01) {diff_alpha = 0.01;} 
						pop_now.get(i_patch).get(list_listeners[l]).setX(pop_now.get(i_patch).get(list_listeners[l]).getX()+ 
								diff_alpha * 
								(pop_now.get(i_patch).get(speaker).getX()- pop_now.get(i_patch).get(list_listeners[l]).getX()));
					}
					nEvent++;
					x_var = utility.sdOpinions(pop_now.get(i_patch));
				}

				// Outcomes of the negotiation process-----------------------
				res_x_consensus[i_gen][i_patch] = utility.meanOpinions(pop_now.get(i_patch)); // We write the fConsensus
				res_t_consensus[i_gen][i_patch] = res_t_consensus[i_gen][i_patch] + nEvent; // We write the tConsensus

				
				// COLLECTIVE ACTION=======================================================================
				// Cost of organisation
				I = res_t_consensus[i_gen][i_patch] * C_t_star;
				// Total benefit of the collective action
				bThTot = (B_max / (1 + Math.exp(-B_inc * (pop_now.get(i_patch).size() - B_mid))));
				bTot = bThTot - I;
				if (bTot < 0) {bTot = 0;}
				if ((B_max / (1 + Math.exp(-B_inc * (100 - B_mid)))) > (B_max
						/ (1 + Math.exp(-B_inc * (200 - B_mid))))) {
					System.out.println("Probleme with bTot function");
				}
				res_B_th_tot[i_gen][i_patch] = bThTot;
				res_B_tot[i_gen][i_patch] = bTot;
				

				// DISTRIBUTION OF RESOURCES================================================================
				// Sorting individuals---------------------------------------
				Collections.sort(pop_now.get(i_patch)); // We sort individuals based on alpha
				// Calcul of personal share
				sum_distrib = 0;
				double[] valueDistribution = new double[pop_now.get(i_patch).size()];
				
				for (int k = 0; k < pop_now.get(i_patch).size(); k++) {
					// We calculate the role l (leader 1 follower 0) from the alpha
					sum_distrib += 1 + d * (pop_now.get(i_patch).get(k).getAlpha()-alpha_f/(alpha_l-alpha_f));
				}
				for (int k = 0; k < pop_now.get(i_patch).size(); k++) { // We calculate the weigthed probabilities
					valueDistribution[k] = (1 + d * (pop_now.get(i_patch).get(k).getAlpha()-alpha_f/(alpha_l-alpha_f))) / sum_distrib;
				}
				//Calcul additional growth rate
				for (int i_ind = 0; i_ind < pop_now.get(i_patch).size(); i_ind++) {
					rB = r_max * (1 - Math.exp(-r_inc * (bTot+(B_tot_pre[i_patch]*0)) * valueDistribution[i_ind]));
					if (rB < 0) {rB = 0;}
					// Calcul fitness
					w = (r_i / (1 + (pop_now.get(i_patch).size() / K))) + rB - C_nego*pop_now.get(i_patch).get(i_ind).getS() - res_h_star[i_gen][i_patch]*C_h; 
					pop_now.get(i_patch).get(i_ind).setW(w);
					
					//Reproduction  (number of offsprings sampled from a poisson distribution)
					if(pop_now.get(i_patch).get(i_ind).getW()<=0) {continue;}
					PoissonDistribution offDistrib = new PoissonDistribution(pop_now.get(i_patch).get(i_ind).getW());
					pop_now.get(i_patch).get(i_ind).setOff(offDistrib.sample());
                }
				B_tot_pre[i_patch] = bTot;
			}
			
			
			// WRITING=======================================================================================================	
			if(i_gen>=first_print&(i_gen==0 || (i_gen+1)%step_print==0)) {
				if(detail==2) {
					for(int i_patch = 0; i_patch < N_patch; i_patch++) {
						// If we need to write but patch is empty, we write a list of NA
						if(pop_now.get(i_patch).isEmpty()) {
							res_temp += "NA,NA,NA,NA,NA,NA,NA,NA,NA" + i_patch + "," + i_gen + "," + seed + "\r\n";
							continue;
						}
						for (int i_ind = 0; i_ind < pop_now.get(i_patch).size(); i_ind++) {
							res_temp = res_temp + pop_now.get(i_patch).get(i_ind).getS()
											+","
											+ pop_now.get(i_patch).get(i_ind).getH()
											+","
											+ pop_now.get(i_patch).get(i_ind).getAlpha()
											+","
											+ pop_now.get(i_patch).get(i_ind).getOff()
											+","
											+ pop_now.get(i_patch).get(i_ind).getW()
											+ ","
											+ res_t_consensus[i_gen][i_patch]
											+","
											+ res_x_consensus[i_gen][i_patch]
											+ ","
											+ res_B_th_tot[i_gen][i_patch]
											+ ","
											+ res_B_tot[i_gen][i_patch]
											+ ","
											+ res_h_star[i_gen][i_patch]
											+","
											+ i_patch
											+","
											+ i_gen
											+","
											+ seed     
											+ "\r\n";
						}
						//Write on file (every patch for detail 2)
						try {
	                        pw.write(res_temp.getBytes());
	                        pw.flush();
	                        } catch (IOException ex) {Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);};
	                        res_temp = "";
					}   
				}  
				else if(detail==1){
					for(int i_patch = 0; i_patch < N_patch; i_patch++) {
						if(pop_now.get(i_patch).isEmpty()) {
							res_temp += "NA,NA,NA,NA,NA,NA,NA,NA" + i_patch + "," + i_gen + "," + seed + "\r\n";
							continue;
						}
						res_temp = res_temp 
								+ utility.skewnessAlpha(pop_now.get(i_patch))
								+ ","
								+ utility.meanS(pop_now.get(i_patch))
								+ ","
								+ utility.meanH(pop_now.get(i_patch))
								+ ","
								+ res_t_consensus[i_gen][i_patch]
								+","
								+ res_x_consensus[i_gen][i_patch]
								+ ","
								+ res_B_th_tot[i_gen][i_patch]
								+ ","
								+ res_B_tot[i_gen][i_patch]
								+ ","
								+ res_h_star[i_gen][i_patch]
								+ ","
								+ pop_now.get(i_patch).size()
								+ ","
								+ i_patch
								+","
								+ i_gen
								+","
								+ seed     
								+ "\r\n";
					}
				}
				else if(detail==0) {
					double[] sizePatch = new double[N_patch];
					for(int i_patch = 0; i_patch<N_patch;i_patch++) {sizePatch[i_patch]=(double)pop_now.get(i_patch).size();}
					res_temp = res_temp + utility.skewnessAlphaGlobal(pop_now)
					+ ","
					+ utility.meanSGlobal(pop_now)
					+ ","
					+ utility.meanHGlobal(pop_now)
					+ ","
					+ utility.mean(res_t_consensus[i_gen])
					+ ","
					+ utility.mean(res_B_th_tot[i_gen])
					+ ","
					+ utility.mean(res_B_tot[i_gen])
					+ ","
					+ utility.mean(res_x_consensus[i_gen])
					+ ","
					+ utility.mean(res_h_star[i_gen])
					+","
					+ utility.mean(sizePatch)
					+","
					+ i_gen
					+","
					+ seed
					+ "\r\n";
				}
				//Write on file (by generation for detail 1 and 0)
                if(detail!=2) {
                	try {
                		pw.write(res_temp.getBytes());
                		pw.flush();
                	} catch (IOException ex) {Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);}
                	res_temp = "";
                }	
			}
			

			//REPRODUCTION=================================================================
			// No offspring for last generation
			if (i_gen == N_gen) {
				break;
			} 
			for (int i_patch = 0; i_patch < N_patch; i_patch++) {
				
				//If patch empty, jump
				if (pop_now.get(i_patch).isEmpty()) {
					continue;
				}
				for (int i_ind = 0; i_ind < pop_now.get(i_patch).size(); i_ind++) {
					for (int i_off = 0; i_off < pop_now.get(i_patch).get(i_ind).getOff(); i_off++) {
						//Migration------------------------------------------------------------
						if (utility.testProb(m) == 1) {
								pop_next.get(utility.randomSampleOther(N_patch, i_patch)).add(new Individual(pop_now.get(i_patch).get(i_ind)));
						}
						else {
							pop_next.get(i_patch).add(new Individual(pop_now.get(i_patch).get(i_ind)));
						}	
					}
				}
			}
			//MUTATION=======================================================================
			pop_now.clear();
			for (int i_patch = 0; i_patch < N_patch; i_patch++) {
				for (int i_ind = 0; i_ind < pop_next.get(i_patch).size(); i_ind++) {
					if(utility.testProb(mu)==1) {pop_next.get(i_patch).get(i_ind).mutateS();}
					if(utility.testProb(mu)==1) {pop_next.get(i_patch).get(i_ind).mutateH();}
				}
				// Non overlapping generations, we remove previous generation and move pop_next to pop_now
				pop_now.add(new ArrayList<>(pop_next.get(i_patch)));
				pop_next.get(i_patch).clear();
			}
		//End SIMULATIONS+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		}
		//Close file within writer
		try {
			pw.closeEntry();
		} catch (IOException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
