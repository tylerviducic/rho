void mxp_samecanvas()

{

    //this generates the canvas that will draw the histograms. You can adjust the spacing between the histograms (if you want it) by editing the last two numbers in the line c1->Divide.

    

    //you may need to make more canvas if you have a ton of histos

    

    TCanvas *c1 = new TCanvas("c1","display",900,700);

    gStyle->SetOptStat(0);

    c1->Divide(7,8,0.001,0.003);

    

    //This line opens the file where your histograms are saved

    TFile *g = TFile::Open("/home/physics/Research/rho/g11/PipPimRHo.root");



    //this loop gets all the histograms from the root file, draws them accordingly

    // you'll need to change the size of the arrays to the number of histos you actually have

    char signal[61];

    char sideband[61];

    char mxp[61];

    for (int k=0; k<=61; k++) //also change your for-loop to the number of histos you have

    {

        stringstream ss;

        ss << k;

        TString str = ss.str();

        

        

        sprintf(mxp,"%s%d","h",k);

//        TH1F *mxp = (TH1F*)g->Get->("h"+str);
		TH1F *mxp = (TH1F*)g->Get("h"+str);

        c1->cd(k+1); //fills the canvas, numbering starts from 1 instead of 0

        mxp->Draw();

        

        //use this part to draw the signal and sidebands on top of each other. Moskov will have you do this eventually so you may as well keep it

        

        sprintf(signal,"%s%d","signal",k);

        //sprintf(sideband,"%s%d","sideband",k);

        

        TH1F *signal = (TH1F*)g->Get("signal"+str);

        //TH1F *sideband = (TH1F*)g->Get("sideband"+str);

        c1->cd(k+1); // fills the canvas, it starts from 1 instead of 0

        signal->SetFillColor(4);

        signal->Draw();

        //sideband->SetFillColor(2);

        //sideband->Draw("same");

         

        

        c1->Update();

    }

}
