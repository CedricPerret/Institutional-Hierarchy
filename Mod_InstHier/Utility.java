package Mod_InstHier;

import static java.lang.Math.exp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;

public class Utility {
    Random randomGenerator;
    private Skewness skewnessCalculator;
    
    public Utility(Random pRandomGenerator){
        this.randomGenerator = pRandomGenerator;
        this.skewnessCalculator = new Skewness();
    }
    
    //
    public double randomDouble(){
        return randomGenerator.nextDouble();
    }
    
    public int randomIntegerRange(int boundExclusive) {
    	return randomGenerator.nextInt(boundExclusive);
    }
    // Probability test
    public int testProb(double pMu){
        if(Math.random() > pMu){
            return 0;                                                           //Nothing
        }
        else{
           return 1;                                                            //Event        
        }
    }
    //Writer
    public List writeFile(List pList){
        return(pList);
    }
    
    
    
    
    public int randomSampleOther(int pArrayL, int pIndex){
        //if(pLArray == 1){System.out.println("BUG randomSampleOther List too short");}
        ArrayList<Integer> resList = new ArrayList();
        for(int i=0; i<pArrayL; i++){resList.add(i);}
        resList.remove(pIndex);
        int index = (int)(Math.floor(Math.random()*(pArrayL-1)));
        return resList.get(index);
        }
    
    //Method to simulate mutation event affecting the social strategy
    public int[] randomSampleOtherList(int pArrayL, int pSampleSize, int pIndex){
        //if(pLArray == 1){System.out.println("BUG randomSampleOther List too short");}
        ArrayList<Integer> resList = new ArrayList();
        for(int i=0; i<pArrayL; i++){resList.add(i);}
        resList.remove(pIndex);
        int indexTemp;
        int[] indexL = new int[pSampleSize];
        for(int i=0; i<pSampleSize; i++){
            indexTemp = (int)(Math.floor(Math.random()*(resList.size())));
            indexL[i]= resList.get(indexTemp);
            resList.remove(indexTemp);
        }
        return indexL;
        }

    public int probSample(double[] pArrayProb, double pKey){             //Binary search algorithm for sampling probability
                            int lower = 0;
                            int upper = pArrayProb.length-1;
                            int mid;
                            while (lower < upper){ 
                                mid = (int)Math.floor((lower + upper )/2);      
                                if((pArrayProb[mid] - pKey) > 0){
                                    upper = mid;
                                }
                                else{
                                    lower = mid + 1;
                                }
                            }
                            return lower;
                        }
    //Calcul of mean
    public double mean(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        return sum / m.length;
    }
    
    //Calcul of variance
    public double sdOpinions(List<Individual> pList){
        double M= 0;
        double S = 0;
        double oldM;
        for(int i = 0; i < pList.size(); i++){
            oldM = M;
            M += (pList.get(i).getX()-M)/(i+1);
            S += (pList.get(i).getX()-M) * (pList.get(i).getX()-oldM);
        }
        return (Math.sqrt(S/(pList.size()-1)));
    }
    

    
    //Calcul of mean of the preferences
    public double meanOpinions(List<Individual> pList){
        double sumF = 0;
        for(int i = 0; i < pList.size(); i++){
            sumF += pList.get(i).getX();
        }
        return(sumF/(pList.size()));
    }
    
    // Method to create a discrete distribution of negative exponential form
    public double[] functionDistribution(int pSize, double pFConsensus){
        double[] valueDistribution2 = new double[pSize];
        double sumDistrib = 0.0d;
        for(int i=0; i < pSize; i++){
            sumDistrib = sumDistrib + exp(pFConsensus * (-i));                  // Sum of the whole share to normalize the final values
        }
        for(int i=0; i < pSize; i++){
            valueDistribution2[i] = exp(pFConsensus * (-i))/sumDistrib;
        }
        return(valueDistribution2);
    }
    
        //Method to mutate a value (between 0 and 1) by normal distribution
    public double mutation(double pMean,double pSigma,double pMinValue,double pMaxValue){
        NormalDistribution mutDis = new NormalDistribution(pMean,pSigma);
        double res = mutDis.sample();
        if(res < pMinValue){res = pMinValue;}
        if(res > pMaxValue){res = pMaxValue;}
        return(res);
    }
    
    public double powerLawNegative(double x, double lambda, double maxSkewness) {
    	return(Math.pow(x, -lambda*maxSkewness));
    }
    
    //Calculate skewness within a patch or pop
    public double skewnessAlpha(List<Individual> pList) {
    	double[] alphaList = new double[pList.size()];
    	for(int i =0; i<pList.size(); i++) {alphaList[i]=pList.get(i).getAlpha();}
    	double res = this.skewnessCalculator.evaluate(alphaList);
    	return(res);
    }
    
    public double skewnessAlphaGlobal(List<List<Individual>> pList) {
    	int totalSize = 0;
    	
    	for (int i = 0; i<pList.size();i++) {totalSize+=pList.get(i).size();}
    	double[] alphaList = new double[totalSize];
    	
    	int k=0;
    	for(int i =0; i<pList.size(); i++) {
    		for(int j=0; j<pList.get(i).size();j++) {
    		alphaList[k]=pList.get(i).get(j).getAlpha();
    		k++;
    		}
    	}
    	double res = this.skewnessCalculator.evaluate(alphaList);
    	return(res);
    }
    
    public double meanH(List<Individual> list) {
    	double res = 0;
    	if(list.isEmpty()) {return res;}
    	for(int i = 0; i < list.size(); i++) {res += list.get(i).getH();}
    	return(res/list.size());
    }
    
    public double meanHGlobal(List<List<Individual>> list) {
    	double res=0;
    	if(list.isEmpty()) {return res;}
    	for(List<Individual> i : list) {res +=  this.meanH(i);}
    	return(res/list.size());
    }
    
    public double meanS(List<Individual> list) {
    	double res = 0;
    	if(list.isEmpty()) {return res;}
    	for(int i = 0; i < list.size(); i++) {res += list.get(i).getS();}
    	return(res/list.size());
    }
    
    public double meanSGlobal(List<List<Individual>> list) {
    	double res=0;
    	if(list.isEmpty()) {return res;}
    	for(List<Individual> i : list) {res += this.meanS(i);}
    	return(res/list.size());
    }
    
}
    
    

