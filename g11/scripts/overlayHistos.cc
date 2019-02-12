void overlayHistos(){
	
	TCanvas *c1 = new TCanvas("c1","display",900,700);
    TCanvas *c2 = new TCanvas("c2","display",900,700);
    TCanvas *c3 = new TCanvas("c3","display",900,700);
    
    gStyle->SetOptStat(0);

    c1->Divide(4,5,0,0);
	c2->Divide(4,5,0,0);
	c3->Divide(4,5,0,0);
	
	TFile *g = TFile::Open("/home/physics/research/rho/g11/scripts/signal_histos.root");
	
	char signal[61];
    char signalcut[61];
    
    for(int i = 0; i < 50; i++){
    	stringstream ss;
        ss << i;
        TString str = ss.str();
        
        sprintf(signal,"%s%d","signal",i);
        sprintf(signalcut,"%s%d","signalcut",i);
        
        TH1F *signal = (TH1F*)g->Get("signal"+str);
        TH1F *signalcut = (TH1F*)g->Get("signalcut"+str);
        
        if(i<21){
        	c1->cd(i+1);
        	signalcut->SetFillColor(4);
        	signal->Draw();
        	signalcut->Draw("SAME");
        }else if(i >=21 && i < 41){
        	c2->cd(i-20);
        	signalcut->SetFillColor(4);
        	signal->Draw();
        	signalcut->Draw("SAME");
        } else{
        	c3->cd(i-40);
        	signalcut->SetFillColor(4);
        	signal->Draw();
        	signalcut->Draw("same");	
        }
    }
}
