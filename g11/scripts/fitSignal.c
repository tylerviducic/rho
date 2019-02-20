void fitSignal(){
	TFile *f = TFile::Open("/home/physics/research/rho/g11/scripts/signal_histos.root");
	TH1F *h1 = (TH1F*)f->Get("signal45");
	
	h1->Sumw2();
    h1->SetLineColor(kBlue);
    h1->SetLineWidth(1);
    h1->GetXaxis()->SetTitle("mx2_PPipPim [GeV^{2}]");
    h1->SetTitle("abs(IM_PipPim - 0.75)<0.005 GeV");
    h1->Draw();
    
    TF1 *g1 = new TF1("photon","gaus",-0.01,0.01);
    g1->SetLineColor(3);
    
    TF1 *g2 = new TF1("pions","gaus",0.01,0.03);
    g2->SetLineColor(4);
    
    TF1 *g3 = new TF1("background","pol2",-0.02,0.04);
    g3->SetLineColor(5);
    
   //defining sum of 2 gaussians & quadratic background
    
    TF1 *total = new TF1("total","gaus(0)+ gaus(3)+pol2(6)", -0.03,0.05);
    total->SetLineColor(1);
    
    // creates parameter array for total function
    double_t par[9];
    
    //fit histogram in range specified above
    h1->Fit(g1,"R");
    h1->Fit(g2,"R+");
    h1->Fit(g3,"R+");
    
    //get parameters of gaussians and polynomial

    g1->GetParameters(&par[0]);
    g2->GetParameters(&par[3]);
    g3->GetParameters(&par[6]);
    
    total->SetParameters(par);
    
    //fit sum of 2 gaussians & quadratic background
    h1->Fit(total,"R+");
    gStyle->SetOptFit(11111);
    
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
    
    cout << " " << "integral = " <<  integral << " +/-" << " " << error << endl;
    cout << "chi^2/ndf = " << total->GetChisquare()/total->GetNDF()<<endl;
 }

