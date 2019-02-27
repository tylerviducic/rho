void plotSignal(){
	
	gStyle->SetOptStat(11111111);    
    TFile *f1=new TFile("/home/physics/research/rho/g11/PipPimRHo.root");
    TTree *G = (TTree*) f1->Get("dataTree");
    
    Double_t mx_P, mx2_PPipPim, mx2_PPipPimGam, Pgam, me_PPipPim, IM_PipPim;
    
    Double_t M_Rho = .770;
    
    G->SetBranchAddress("mx_P",&mx_P);
    G->SetBranchAddress("mx2_PPipPim",&mx2_PPipPim);
    G->SetBranchAddress("mx2_PPipPimGam",&mx2_PPipPimGam);
    G->SetBranchAddress("Pgam",&Pgam);
    G->SetBranchAddress("me_PPipPim",&me_PPipPim);
    G->SetBranchAddress("IM_PipPim",&IM_PipPim);
    
    TFile *f2=new TFile("signal_histos.root","RECREATE");
    
    TH1F *signal[50];
    TH1F *signalcut[50];
    
    char hname[61];
	char cname[61];
	char hnamecut[80];
	char cnamecut[80];
	
	
	double e;
	for(int i = 0; i < 50; i++){
	e = 0.3 + (double(i)/100);
		sprintf(hname, "signal%d", i);
		sprintf(hnamecut, "signalcut%d", i);
		sprintf(cname, "mx2_PPipPim in region of |IM_PipPim - %.2f| < 0.005", e);
		sprintf(cnamecut, "mx2_PPipPim in region of |IM_PipPim - %.2f| < 0.005 with mx_P < .730", e); 
		signal[i] = new TH1F(hname, cname, 100, -0.05, 0.05);
		signalcut[i] = new TH1F(hnamecut, cnamecut, 100, -0.05, 0.05);
	}
	
	Double_t nEvent=G->GetEntries();
	
	double Pgamcut = 0.1;
	double me_PPipPimcut = 0.1;
	double me_PPipPimPGamcut = 0.1;
	double mx_Pcut = 0.12;
	double mx2_PPipPimGamcut = 0.001;
	double wcut = 0.73;
	
	
	for(Int_t i = 0; i < nEvent; i++){
		G->GetEntry(i);
		for(int k = 0; k < 50; k++){
			e = 0.3 +(double(k)/100);
			if(Pgam > Pgamcut && me_PPipPim > me_PPipPimcut && abs(me_PPipPim-Pgam) < me_PPipPimPGamcut && abs(mx_P-M_Rho) < mx_Pcut && abs(mx2_PPipPimGam)<mx2_PPipPimGamcut && abs(IM_PipPim - e) < 0.005){
			signal[k]->Fill(mx2_PPipPim);
			}
			if(Pgam > Pgamcut && me_PPipPim > me_PPipPimcut && abs(me_PPipPim-Pgam) < me_PPipPimPGamcut && abs(mx_P-M_Rho) < mx_Pcut && abs(mx2_PPipPimGam)<mx2_PPipPimGamcut && abs(IM_PipPim - e) < 0.005 && mx_P < wcut){
			signalcut[k]->Fill(mx2_PPipPim);
			}
		}
	}
   
   
   f2->Write();
   f2->Close();
    
}
