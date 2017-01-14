public class ProcessRequest {
	private RequestGroup reqN[];
	OutputInFile of=new OutputInFile();
	String outputName="processReq.txt";
	public RequestGroup[] getRG() {
		return reqN;
	}

	public int processReq(int oldRequest[][],AllPairShortestPath ob, int capacity)
	{
	//	int total_revenue=0;
		int i,j;
		Request reqOb[]=new Request[oldRequest.length];
		Sort sort=new Sort();
		int order[]={0,3,2,1};
		
		//sort the request in the order of startLoc, endTime, startTime and destination
		int request[][]=sort.insertionSort2D(oldRequest, order);
		int count_src[]=new int[ob.getShortestPathMatrix().length];
		
		for(i=0;i<request.length;i++)
		{
			count_src[request[i][0]-1]++; //keep a count of all requests beginning at particular startLoc
			
//			of.outputFile("ReqNo "+(sort.getSortedRequestIndex()[i]+1)+":"+request[i][0]+","+request[i][1]+","+request[i][2]+","+request[i][3],outputName);
			//Form Request Object
			reqOb[i]=new Request();
			reqOb[i].setCost(ob.getMinChargeForRequest(request[i][0], request[i][1]));
			reqOb[i].setDest(request[i][1]);
			reqOb[i].setSource(request[i][0]);
			reqOb[i].setStartTime(request[i][2]);
			reqOb[i].setEndTime(request[i][3]);
			reqOb[i].setReqNo(sort.getSortedRequestIndex()[i]+1);
			reqOb[i].setTaken(false);
		}
		reqN=new RequestGroup[request.length];
		int count=0;
		int temp=0;
		int l=0;
		int noOfRqG=0;
	
		for(i=0;i<count_src.length;i++) //For all request beginning at same startLoc
		{
			for(j=temp,l=0;l<count_src[i];j++,l++)
			{
				if(ob.notReachable(reqOb[j].getSource(), reqOb[j].getDest())) 
				//if there is no path from given source to destination, ignore the request and move ahead 
				{
					temp++;
					reqOb[j].setTaken(true);					
				}
				if(!reqOb[j].isTaken())
				//if the present requestObj is not yet taken in any of the requestGroup, check all other requests compatible with it and form
				//its group
				{
				temp++;
				count=0;
				Request path[]=new Request[capacity];
				int pathTrav[]=new int[capacity+1];
				//form a requestGroup for the present request
				pathTrav[0]=reqOb[j].getSource();
				reqN[noOfRqG]=new RequestGroup();
				reqN[noOfRqG].setSmallestEndTime(reqOb[j].getEndTime());
				reqN[noOfRqG].setLargestStartTime(reqOb[j].getStartTime());
				reqN[noOfRqG].setRevenue(ob.getMinChargeForRequest(reqOb[j].getSource(), reqOb[j].getDest()));
				reqN[noOfRqG].setVisited(false);
				reqN[noOfRqG].setCount(count+1);
				reqN[noOfRqG].setStartLoc(reqOb[j].getSource());
				reqOb[j].setTaken(true);
				path[count]=reqOb[j];
				pathTrav[count+1]=reqOb[j].getDest();
				
				//check remaining request that have same startLoc that are time compatible with present request and add them to the requestGroup
					for(int k=j+1,m=1;m<count_src[i]-l;k++,m++)
					{
						if(count<capacity-1 && !reqOb[k].isTaken() && reqOb[k].getStartTime()<= reqN[noOfRqG].getSmallestEndTime() && !ob.notReachable(reqOb[j].getDest(), reqOb[k].getDest()))
						{
							reqOb[k].setTaken(true);
							count++;
							reqN[noOfRqG].setCount(count+1);
							reqN[noOfRqG].setRevenue(reqN[noOfRqG].getRevenue()+ob.getMinChargeForRequest(reqOb[j].getSource(), reqOb[k].getDest()));
							path[count]=reqOb[k];

							pathTrav[count+1]=reqOb[k].getDest();
							if(reqOb[k].getStartTime()>reqN[noOfRqG].getLargestStartTime())
								reqN[noOfRqG].setLargestStartTime(reqOb[k].getStartTime());
							temp++;
						}
					}
					ob.formOrder(pathTrav, count+1,reqN[noOfRqG],path); //form order so that the closest dest is visited first
				//	total_revenue+=reqN[noOfRqG].getRevenue();
					noOfRqG++;
				}
			}
		}
		
		reqN=sort.sortRequestGroup(reqN, noOfRqG); //sort request group in the order of smallestEndTime and largestStartTime
/*		for(i=0;i<noOfRqG;i++)
			reqN[i].print_RG(i+1);
*/	//	System.out.println("Total"+total_revenue);
		return noOfRqG;
	}

}
