package loan;

import database.DBOperations;
import net.sf.json.JSONObject;

public class Algorithm {
	
	private final int MAXIMUM_OUTCOME_PERCENTAGE = 50;
	
	private float mMonthlyIncome;
	private float mMonthlyOutcome;
	private float mMonthlyLoan;
	
	private int mDomainId;
	private int mExperienceId;
	
	private boolean isIncomeRaising;
	
	
	public Algorithm(JSONObject info) {
		boolean alteCredite = info.getInt("alteCredite") == 1;
		float sumaAlteRate = (float)info.getDouble("sumaAlteRate");
		
		setMonthlyOutcome(alteCredite, sumaAlteRate);
		
		float venitLC = (float)info.getDouble("venitLC");
		float venitLC_1 = (float)info.getDouble("venitLC_1");
		float venitLC_2 = (float)info.getDouble("venitLC_2");
		float bonus3M = (float)info.getDouble("bonus3M");
		float venitAnAnterior = (float)info.getDouble("venitAnAnterior");
		
		setMonthlyIncome(venitLC, venitLC_1, venitLC_2, bonus3M);
		setSalaryRaisingTrend(venitAnAnterior);
		
		mExperienceId = info.getInt("expId");
		mDomainId = info.getInt("domId");
		
		float sumaCreditata = (float)info.getDouble("sumaCreditata");
		int nrRate = info.getInt("nrRate");
		float dae = (float)info.getDouble("dae");
		
		setMonthlyLoan(sumaCreditata, nrRate, dae);
	}
	
	private void setMonthlyIncome(float venitLC, float venitLC_1, float venitLC_2, float bonus3M) {
		mMonthlyIncome = ((venitLC + venitLC_1 + venitLC_2) / 3) + (bonus3M / 3);
	}
	
	private void setMonthlyOutcome(boolean alteCredite, float sumaAlteRate) {
		mMonthlyOutcome = alteCredite ? sumaAlteRate : 0;
	}
	
	private void setMonthlyLoan(float sumaCreditata, int nrRate, float dae) {
		float years = nrRate / 12f;
		float extraPay = years * dae;
		float totalPay = sumaCreditata + extraPay;
		
		mMonthlyLoan = totalPay / nrRate;
	}
	
	private void setSalaryRaisingTrend(float venitAnAnterior) {
		isIncomeRaising = (venitAnAnterior / 12) > mMonthlyIncome;
	}
	
	private float getMaximumMonthlyOutcome() {
		return mMonthlyIncome * MAXIMUM_OUTCOME_PERCENTAGE / 100;
	}
	
	private boolean isOutcomeExceeded() {
		return getMaximumMonthlyOutcome() < mMonthlyOutcome;
	}
	
	private boolean isOutcomeExceeded(float amount) {
		return getMaximumMonthlyOutcome() < (mMonthlyOutcome + amount);
	}
	
	public int computeStatus() {
		
		//cannot afford a new loan
		if(isOutcomeExceeded()) return 0;
		
		//the new loan is too expensive
		if(isOutcomeExceeded(mMonthlyLoan)) return 0;
		
		int domainCoeff = DBOperations.getDomainCoeffById(mDomainId); // (0, 10] 
		int expCoeff = DBOperations.getExperienceCoeffById(mExperienceId); // (0, 5]
		
		int backgroundCoeff = domainCoeff * expCoeff; // (0, 50]
		if(isIncomeRaising) backgroundCoeff *= 2; // (0, 100]
		
		int incomeCoeff = getIncomeWeight(mMonthlyIncome); // (0, 100]
		
		int coeff = (int) ((backgroundCoeff * 0.25) + (incomeCoeff * 0.75)); // (0, 100]
		
		if(coeff <= 35) return 0;
		return coeff < 66 ? 1 : 2;
	}
	
	private int getIncomeWeight(float number) {
		int nr = (int)number;
		int length = 0;
		int firstDigit = 1;
		
		while(nr > 0) {
			nr /= 10;
			length ++;
			
			if(nr < 10 && nr > 0) {
				firstDigit = nr;
			}
		}
		
		return length * 15 + firstDigit;
	}
}
