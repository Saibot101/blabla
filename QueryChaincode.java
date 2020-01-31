import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import User.UserContext;
import org.hyperledger.fabric.sdk.*;

import Util.Util;
import Config.Config;

public class QueryChaincode {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	public static void main(String args[]) {
		try {
            Util.cleanUp();

            //Setup CAClient
			String caUrl = Config.CA_ORG1_URL;

			CAClient caClient = new CAClient(caUrl, new Properties());

			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);

			//Setup CAClient mit Admin Context + enroll admin
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

			//Setup Client
			//FabricClient == HFClient where adminUserContext als setUserContext vervendet wird
			FabricClient fabClient = new FabricClient(adminUserContext);

            //Setup ChannelClient for Channel with name
            ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peerorg1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			Peer peerorg2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL);
            //EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            channel.addPeer(peerorg1);
			channel.addPeer(peerorg2);
            //channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();





            //Query
			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying ...");

			QueryByChaincodeRequest request = fabClient.getInstance().newQueryProposalRequest();
			ChaincodeID ccid = ChaincodeID.newBuilder().setName("acme_cc_s1").build();
			request.setChaincodeID(ccid);
			request.setFcn("query");
			if (args != null)
				request.setArgs(args);

			Collection<ProposalResponse> response = channel.queryByChaincode(request);


			//Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode("fabcar", "queryAllCars", null);
			for (ProposalResponse pres : response) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}

			/*
			Thread.sleep(100000);
			String[] args1 = {"CAR1"};
			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for a car - " + args1[0]);
			*/
			/*
			//Output
			Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode("fabcar", "queryCar", args1);
			for (ProposalResponse pres : responses1Query) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}*/

			/*
			Which properties do i need:
			CAUrl
			UserName
			OrgName
			MspId
			Secret = password
			ChannelName
			ChainCodeName
			FunctionName
			FunctionArgs
			 */
			// enroll is only 1 time possible, after that registerUser
            //for enroll https://github.com/hyperledger/fabric-sdk-java/blob/master/src/main/java/org/hyperledger/fabric_ca/sdk/HFCAClient.java ab line 420


            /*
			//instead of enrolladmin, we can register and enroll user
			CAClient newCAClient = new CAClient(caUrl, null);
			UserContext newUserContext = new UserContext();
			newUserContext.setName("user");
			newUserContext.setAffiliation("org1"); // from config
			newUserContext.setMspId("Org1MSP"); // from config

			newUserContext = newCAClient.enrollUser(newUserContext,"secret"); //i don't knwo what they mean with secret

			FabricClient newFabClient = new FabricClient(newUserContext);

			ChannelClient newChannelClient = newFabClient.createChannelClient(Config.CHANNEL_NAME);

			// i think not needed, query is going over ChannelClient
			Channel newChannel = newChannelClient.getChannel();
			Peer newPeer = newFabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			EventHub newEventHub = newFabClient.getInstance().newEventHub("eventhub01","grpc://localhost:7053");
			Orderer newOrderer = newFabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
			newChannel.addPeer(newPeer);
			newChannel.addEventHub(newEventHub);
			newChannel.addOrderer(newOrderer);
			newChannel.initialize();

            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for all cars ...");
            Collection<ProposalResponse>  responsesQuery = newChannelClient.queryByChainCode("fabcar", "queryAllCars", null);
            for (ProposalResponse pres : responsesQuery) {
                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
                Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
            }

            Thread.sleep(10000);
            String[] args1 = {"CAR1"};
            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for a car - " + args1[0]);


            //Output
            Collection<ProposalResponse>  responses1Query = newChannelClient.queryByChainCode("fabcar", "queryCar", args1);
            for (ProposalResponse pres : responses1Query) {
                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
                Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
            }*/





			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
