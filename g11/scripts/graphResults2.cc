void graphResults2(){
	
	TFile *f1 = TFile::Open("/media/tylerviducic/Elements/g11/fittedSignals.root");
	
	TCanvas *c1 = new TCanvas;
	//blue is pol2 - total
	//red is pol3 - total2
	
	int size = 43;
	
	Double_t Nphotons[size]; 
	Double_t Ne[size];
	Double_t Nphotons2[size]; 
	Double_t Ne2[size];
	Double_t Egam[size];
	
	for(int i = 7; i <25; i++){
		
		TString sig;
		sig.Form("%d", i);
		
		TH1F *h1 = (TH1F*)f1->Get("signalcut" + sig);
		
		h1->Draw();
		TF1 *total = h1->GetFunction("total");
		TF1 *total2 = h1->GetFunction("total2");
		
		//getting parameters and parameter errors from fit
    
    	Double_t p0 = total->GetParameter(0); //amplitue
    	Double_t p1 = total->GetParameter(1);
    	Double_t p2 = total->GetParameter(2); //sigma
    	
    	Double_t e0 = total->GetParError(0); //error on amplitude
    	Double_t e1 = total->GetParError(1);
    	Double_t e2 = total->GetParError(2); //error on sigma
    	
    	Double_t p02 = total2->GetParameter(0); //amplitue
    	Double_t p12 = total2->GetParameter(1);
    	Double_t p22 = total2->GetParameter(2); //sigma
    	
    	Double_t e02 = total2->GetParError(0); //error on amplitude
    	Double_t e12 = total2->GetParError(1);
    	Double_t e22 = total2->GetParError(2); //error on sigma
		
	    //calculating integral & error from parameters of fit
    	double integral;
    	double s=p2;
    	double ds=e2;
    	double a=p0;
    	double da=e0;
    	
        double pi=4.0*atan(1.0);
    	double b=0.00125; //is this binsize
    	integral = (sqrt(2*pi)*s*a) / b ;
    	
    	double error;
    	error = sqrt((ds/s)*(ds/s) + (da/a)*(da/a))*integral;
    	
    	double integral2;
    	double s2=p22;
    	double ds2=e22;
    	double a2=p02;
    	double da2=e02;
    	
    	integral2 = (sqrt(2*pi)*s2*a2) / b ;
    	
    	double error2;
    	error2 = sqrt((ds2/s2)*(ds2/s2) + (da2/a2)*(da2/a2))*integral2;
    	
    	Nphotons[i-7] = integral;
    	Ne[i-7] = error;
    	
    	Nphotons2[i-7] = integral2;
    	Ne2[i-7] = error2;	
    	
    	double im = (double)(i + 30)/100;
    	
    	Egam[i-7] = (.770 * .770 - im*im)/(2 * .770);
	}
	
	
	for(int i = 25; i < 50; i++){
		
		TString sig;
		sig.Form("%d", i);
		
		TH1F *h1 = (TH1F*)f1->Get("signal" + sig);
		
		h1->Draw();
		TF1 *total = h1->GetFunction("total");
		TF1 *total2 = h1->GetFunction("total2");
		
		//getting parameters and parameter errors from fit
    
    	Double_t p0 = total->GetParameter(0); //amplitue
    	Double_t p1 = total->GetParameter(1);
    	Double_t p2 = total->GetParameter(2); //sigma
    	
    	Double_t e0 = total->GetParError(0); //error on amplitude
    	Double_t e1 = total->GetParError(1);
    	Double_t e2 = total->GetParError(2); //error on sigma
    	
    	Double_t p02 = total2->GetParameter(0); //amplitue
    	Double_t p12 = total2->GetParameter(1);
    	Double_t p22 = total2->GetParameter(2); //sigma
    	
    	Double_t e02 = total2->GetParError(0); //error on amplitude
    	Double_t e12 = total2->GetParError(1);
    	Double_t e22 = total2->GetParError(2); //error on sigma
		
	    //calculating integral & error from parameters of fit
    	double integral;
    	double s=p2;
    	double ds=e2;
    	double a=p0;
    	double da=e0;
    	
        double pi=4.0*atan(1.0);
    	double b=0.001;
    	integral = (sqrt(2*pi)*s*a) / b ;
    	
    	double error;
    	error = sqrt((ds/s)*(ds/s) + (da/a)*(da/a))*integral;
    	
    	double integral2;
    	double s2=p22;
    	double ds2=e22;
    	double a2=p02;
    	double da2=e02;
    	
    	integral2 = (sqrt(2*pi)*s2*a2) / b ;
    	
    	double error2;
    	error2 = sqrt((ds2/s2)*(ds2/s2) + (da2/a2)*(da2/a2))*integral2;
    	
    	Nphotons[i-7] = integral;
    	Ne[i-7] = error;
    	
    	Nphotons2[i-7] = integral2;
    	Ne2[i-7] = error2;
    	
    	double im = (double)(i + 30)/100;
    	
    	Egam[i-7] = (.770 * .770 - im*im)/(2 * .770);
	}	
	
	
	TGraphErrors *g1 = new TGraphErrors(44, Egam, Nphotons,0, Ne);
	g1->SetTitle("N vs. Egam");
	g1->GetXaxis()->SetTitle("E_gamma [GeV]");
	g1->SetLineColor(4);
	g1->SetLineWidth(3);
	
	g1->Draw("AP");
	
	TGraphErrors *g2 = new TGraphErrors(44, Egam, Nphotons2,0, Ne2);
	g2->SetLineColor(2);
	
	g2->Draw("P");
}

