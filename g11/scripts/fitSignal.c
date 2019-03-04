void fitSignal(){
	TFile *f = TFile::Open("/home/physics/research/rho/g11/scripts/signal_histos.root");
	TFile *f1 = new TFile("/home/physics/research/rho/g11/scripts/fittedSignals.root", "UPDATE");
	//TFile *f1 = new TFile("/home/physics/research/rho/g11/scripts/fittedSignals.root", "RECREATE"); // uncomment if starting over for some reason
	
	TCanvas *c1 = new TCanvas;
	
	TH1F *h1 = (TH1F*)f->Get("signalcut37");
	TH1F *h2 = new TH1F("h2", "h2", 50, -0.02, 0.03);
	TH1F *h3 = new TH1F("h3", "h3", 50, -0.02, 0.03);
	
	Double_t min = -0.015;
	Double_t max = 0.025;
	
	h1->Sumw2();
    h1->SetLineColor(kBlue);
    h1->SetMarkerStyle(8);
    h1->SetLineWidth(1);
    h1->GetXaxis()->SetTitle("mx2_PPipPim [GeV^{2}]");
    h1->SetTitle("abs(IM_PipPim - 0.67)<0.005 GeV w/ cut mxp < 0.730");
    h1->Draw();
    
    TF1 *g1 = new TF1("photon","gaus",-0.005,0.005);
    g1->SetLineColor(1);
    
    TF1 *g2 = new TF1("pions","gaus",0.01,0.03);
    g2->SetLineColor(1);
    
    TF1 *g3 = new TF1("background1","pol2",-0.02,0.04);
    g3->SetLineColor(1);
    
    TF1 *g4 = new TF1("background2", "pol3", -0.02, 0.04);	
    g4->SetLineColor(1);
    
   //defining sum of 2 gaussians & quadratic background
    
    TF1 *total = new TF1("total","gaus(0)+ gaus(3)+pol2(6)", min, max);
    total->SetLineColor(3);
    
    TF1 *total2 = new TF1("total2","gaus(0)+ gaus(3)+pol3(6)", min, max);
    total->SetLineColor(4);
    
    total->SetLineWidth(5);
    total2->SetLineWidth(2);
    
    // creates parameter array for total function
    double_t par[9];
    double_t par2[10];
    
    //fit histogram in range specified above
    h1->Fit(g1,"R");
    h1->Fit(g2,"R+");
    h1->Fit(g3,"R+");
    h1->Fit(g4,"R+");
    
    //get parameters of gaussians and polynomial

    g1->GetParameters(&par[0]);
    g2->GetParameters(&par[3]);
    g3->GetParameters(&par[6]);
    
    g1->GetParameters(&par2[0]);
    g2->GetParameters(&par2[3]);
    g4->GetParameters(&par2[6]);
    
    total->SetParameters(par);
    total2->SetParameters(par2);
    
    //fit sum of 2 gaussians & quadratic background
    h1->Fit(total,"R");
    h1->Fit(total2, "R+");
    gStyle->SetOptFit(1100);
    
    //getting parameters and parameter errors from fit
    
    Double_t p0 = total->GetParameter(0); //amplitue
    Double_t p1 = total->GetParameter(1);
    Double_t p2 = total->GetParameter(2); //sigma
    //double_t p3 = total->GetParameter(3); //amplitue pion gaus
    //double_t p4 = total->GetParameter(4);
    //double_t p5 = total->GetParameter(5); //sigma
    
    Double_t e0 = total->GetParError(0); //error on amplitude
    Double_t e1 = total->GetParError(1);
    Double_t e2 = total->GetParError(2); //error on sigma
    
    Double_t p02 = total2->GetParameter(0); //amplitue
    Double_t p12 = total2->GetParameter(1);
    Double_t p22 = total2->GetParameter(2); //sigma
    //double_t p3 = total->GetParameter(3); //amplitue pion gaus
    //double_t p4 = total->GetParameter(4);
    //double_t p5 = total->GetParameter(5); //sigma
    
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
    double b=0.00125;
    integral = (sqrt(2*pi)*s*a) / b ;
    double error;
    
    error = sqrt((ds/s)*(ds/s) + (da/a)*(da/a))*integral;
    
    cout << " " << "integral(blue) = " <<  integral << " +/-" << " " << error << endl;
    cout << "chi^2/ndf = " << total->GetChisquare()/total->GetNDF()<<endl;
    
    double integral2;
    double s2=p22;
    double ds2=e22;
    double a2=p02;
    double da2=e02; 
   
    //double pi=4.0*atan(1.0);
    //double b=0.00125;
    integral2 = (sqrt(2*pi)*s2*a2) / b ;
    double error2;
    
    error2 = sqrt((ds2/s2)*(ds2/s2) + (da2/a2)*(da2/a2))*integral2;
    
    cout << " " << "integral2(red) = " <<  integral2 << " +/-" << " " << error2 << endl;
    cout << "chi^2/ndf2 = " << total2->GetChisquare()/total2->GetNDF()<<endl;
    
    TPaveStats *ps = (TPaveStats*)c1->GetPrimitive("stats");
    ps->SetName("myStats");
    TList *listOfLines = ps->GetListOfLines();
    
    TString chi1;
    TString chi2;
    TString int1;
    TString int2;
    TString er1;
    TString er2;
    
    Double_t chi1ndf = total->GetChisquare()/total->GetNDF();
    Double_t chi2ndf = total2->GetChisquare()/total2->GetNDF();

    chi1.Form("%f", chi1ndf);
    chi2.Form("%f", chi2ndf);
    int1.Form("%f", integral);
    int2.Form("%f", integral2);
    er1.Form("%f", error);
    er2.Form("%f", error2);

    TLatex *chi_1 = new TLatex(0,0, "chi1 = " + chi1);
    TLatex *chi_2 = new TLatex(0,0, "chi2 = " + chi2);
    TLatex *int_1 = new TLatex(0,0, "int1(pol2) = " + int1 + " +/- "  + er1);
    TLatex *int_2 = new TLatex(0,0, "int2(pol3) = " + int2 + " +/- "+ er2);
    
    int_1->SetTextSize(0.03);
    int_2->SetTextSize(0.03);
    
    listOfLines->Add(chi_1);
    listOfLines->Add(chi_2);
    listOfLines->Add(int_1);
    listOfLines->Add(int_2);
    
    h1->SetStats(0);
    c1->Modified(); 
    
    //h1->Write();
    h1->Write("", TObject::kOverwrite); // uncomment if overwriting an existing histo in 
    
    h2->Add(total);
    h3->Add(total2);
    h2->Divide(h3);
    //h2->Draw();
    
    
 }

