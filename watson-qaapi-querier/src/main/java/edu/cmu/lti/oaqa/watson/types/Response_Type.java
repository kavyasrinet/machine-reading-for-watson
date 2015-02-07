
/* First created by JCasGen Sun Apr 27 16:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.watson.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** 
 * Updated by JCasGen Sun Apr 27 16:40:19 EDT 2014
 * @generated */
public class Response_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Response_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Response_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Response(addr, Response_Type.this);
  			   Response_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Response(addr, Response_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Response.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.watson.types.Response");
 
  /** @generated */
  final Feature casFeat_responsedJson;
  /** @generated */
  final int     casFeatCode_responsedJson;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getResponsedJson(int addr) {
        if (featOkTst && casFeat_responsedJson == null)
      jcas.throwFeatMissing("responsedJson", "edu.cmu.lti.oaqa.watson.types.Response");
    return ll_cas.ll_getStringValue(addr, casFeatCode_responsedJson);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setResponsedJson(int addr, String v) {
        if (featOkTst && casFeat_responsedJson == null)
      jcas.throwFeatMissing("responsedJson", "edu.cmu.lti.oaqa.watson.types.Response");
    ll_cas.ll_setStringValue(addr, casFeatCode_responsedJson, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Response_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_responsedJson = jcas.getRequiredFeatureDE(casType, "responsedJson", "uima.cas.String", featOkTst);
    casFeatCode_responsedJson  = (null == casFeat_responsedJson) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_responsedJson).getCode();

  }
}



    