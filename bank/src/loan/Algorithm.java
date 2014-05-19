package loan;

import java.util.Calendar;
import java.util.GregorianCalendar;

import database.DBOperations;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Algorithm {
	
	private final int MAXIMUM_OUTCOME_PERCENTAGE = 50;
	
	private float mMonthlyIncome;
	private float mMonthlyOutcome;
	private float mMonthlyLoan;
	
	private int mDomainId;
	private int mExperienceId;
	
	private boolean mHasDelayedPays;
	private boolean mHadOtherLoans;
	
	private boolean isIncomeRaising;
	
	
	public Algorithm(JSONObject info) {
		boolean alteCredite = info.getInt("alteCredite") == 1;
		float sumaAlteRate = 0;
		
		if(alteCredite) {
			sumaAlteRate = (float)info.getDouble("sumaAlteRate");
		}
		
		setMonthlyOutcome(alteCredite, sumaAlteRate);
		
		float venitLC = (float)info.getDouble("venitLC");
		float venitLC_1 = (float)info.getDouble("venitLC1");
		float venitLC_2 = (float)info.getDouble("venitLC2");
		float bonus3M = (float)info.getDouble("bonus3L");
		float venitAnAnterior = (float)info.getDouble("venitAnAnterior");
		
		setMonthlyIncome(venitLC, venitLC_1, venitLC_2, bonus3M);
		setSalaryRaisingTrend(venitAnAnterior);
		
		mExperienceId = info.getInt("expId");
		mDomainId = info.getInt("domId");
		
		mHadOtherLoans = info.getInt("crediteAnterioare") == 1;
		mHasDelayedPays = info.getInt("intarzieri") == 1;
		
		float sumaCreditata = (float)info.getDouble("sumaCredit");
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
		if(mHadOtherLoans) coeff += 5;
		if(mHasDelayedPays) coeff -= 10;
		
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
	
	public static JSONObject createLoanRates(float credit, float dae, int nrRate) {
		float extraPay = credit * dae / 100;
		float total = credit + extraPay;
		float monthlyMediumExtraPay = extraPay / nrRate;
		float monthlyMediumCreditPay = credit / nrRate;
		float monthlyDifference = (float) (total / Math.pow(10, 4));
		float monthlyMinExtraPay = (float) (total / Math.pow(10, 3));
		float monthlyMaxExtraPay = monthlyDifference * nrRate + monthlyMinExtraPay;
		float monthlyMinCreditPay = monthlyMediumCreditPay + monthlyMediumExtraPay - monthlyMaxExtraPay;
		float monthlyRate = monthlyMediumCreditPay + monthlyMediumExtraPay;
		
		JSONObject loanReturnPlan = new JSONObject();
		
		loanReturnPlan.put("sumaCredit", credit);
		loanReturnPlan.put("dae", dae);
		loanReturnPlan.put("nrRate", nrRate);
		
		Calendar cal = new GregorianCalendar();
		String year = cal.get(Calendar.YEAR) + "";
		int rawMonth = cal.get(Calendar.MONTH) + 1;
		String month = (rawMonth < 10 ? "0" : "") + rawMonth;
		int rawDay = cal.get(Calendar.DAY_OF_MONTH);
		String day = (rawDay < 10 ? "0" : "") + rawDay;
		cal.add(Calendar.MONTH, 1);
		
		loanReturnPlan.put("dataAcord", String.format("%s-%s-%s", year, month, day));
		
		JSONArray rates = new JSONArray();
		for(int i = 0; i < nrRate; i++) {
			JSONObject rata = new JSONObject();
			rata.put("nr", i + 1);
			
			year = cal.get(Calendar.YEAR) + "";
			rawMonth = cal.get(Calendar.MONTH) + 1;
			month = (rawMonth < 10 ? "0" : "") + rawMonth;
			rawDay = cal.get(Calendar.DAY_OF_MONTH);
			day = (rawDay < 10 ? "0" : "") + rawDay;
			cal.add(Calendar.MONTH, 1);
			
			rata.put("scadenta", String.format("%s-%s-%s", year, month, day));
			rata.put("dobanda", monthlyMaxExtraPay - (i * monthlyDifference));
			rata.put("rambursat", monthlyMinCreditPay + (i * monthlyDifference));
			rata.put("total", monthlyRate);
			rata.put("achitat", 0);
			rata.put("dataAchitare", "");
			rates.add(rata);
		}
		loanReturnPlan.put("rate", rates);
		
		return loanReturnPlan;
	}
}
