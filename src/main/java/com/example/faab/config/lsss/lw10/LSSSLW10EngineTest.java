package com.example.faab.config.lsss.lw10;


import com.example.faab.config.AccessControlEngine;
import com.example.faab.config.AccessControlParameter;
import com.example.faab.config.UnsatisfiedAccessControlException;
import com.example.faab.config.parser.ParserUtils;
import com.example.faab.config.parser.PolicySyntaxException;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class  LSSSLW10EngineTest {
    @Test
    public void test() {
        //Obtain PairingParameters from /params/a_160_512.properties
        PairingParameters pairingParameters = PairingFactory.getPairingParameters("a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParameters);
        String accessPolicyString = "((H1 AND P1 AND (D1 OR D2)) OR (H2 AND ((P2 AND D3) OR (P3 AND D4))))";// "((0 and 1 and 2) and (3 or 4 or 5) and (6 and 7 and (8 or 9 or 10 or 11)))";
        String[] satisfiedRhos = new String[] {"H1", "P1", "D1"};//{"0", "1", "2", "4", "6", "7", "10"};
        //Using Lewko-Waters LSSS
        AccessControlEngine accessControlEngine = LSSSLW10Engine.getInstance();
        try {
            //parse access policy
            int[][] accessPolicy = ParserUtils.GenerateAccessPolicy(accessPolicyString);
            System.out.println(accessPolicy);
            String[] rhos = ParserUtils.GenerateRhos(accessPolicyString);
            System.out.println(rhos);
            AccessControlParameter accessControlParameter = accessControlEngine.generateAccessControl(accessPolicy, rhos);
            System.out.println(accessControlParameter);
            //secret sharing
            Element secret = pairing.getZr().newRandomElement().getImmutable();
            Map<String, Element> lambdaElementsMap = accessControlEngine.secretSharing(pairing, secret, accessControlParameter);

            //Secret reconstruction
            Map<String, Element> omegaElementsMap = accessControlEngine.reconstructOmegas(pairing, satisfiedRhos, accessControlParameter);
            Element reconstructedSecret = pairing.getZr().newZeroElement().getImmutable();
            for (String eachAttribute : satisfiedRhos) {
                if (omegaElementsMap.containsKey(eachAttribute)) {
                    reconstructedSecret = reconstructedSecret.add(lambdaElementsMap.get(eachAttribute).mulZn(omegaElementsMap.get(eachAttribute))).getImmutable();
                }
            }
            Assert.assertEquals(secret, reconstructedSecret);
        } catch (UnsatisfiedAccessControlException e) {
            // throw if the given attribute set does not satisfy the access policy represented by accress tree.
            System.out.println("用户属性集不满足访问策略");
        } catch (PolicySyntaxException e) {
            // throw if invalid access policy representation.
            System.out.println("访问策略格式无效");
        }
    }
}
