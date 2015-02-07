
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
public class Query_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Query_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Query_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Query(addr, Query_Type.this);
  			   Query_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Query(addr, Query_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Query.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.watson.types.Query");
 
  /** @generated */
  final Feature casFeat_synchronous;
  /** @generated */
  final int     casFeatCode_synchronous;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getSynchronous(int addr) {
        if (featOkTst && casFeat_synchronous == null)
      jcas.throwFeatMissing("synchronous", "edu.cmu.lti.oaqa.watson.types.Query");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_synchronous);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSynchronous(int addr, boolean v) {
        if (featOkTst && casFeat_synchronous == null)
      jcas.throwFeatMissing("synchronous", "edu.cmu.lti.oaqa.watson.types.Query");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_synchronous, v);}
    
  
 
  /** @generated */
  final Feature casFeat_question;
  /** @generated */
  final int     casFeatCode_question;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getQuestion(int addr) {
        if (featOkTst && casFeat_question == null)
      jcas.throwFeatMissing("question", "edu.cmu.lti.oaqa.watson.types.Query");
    return ll_cas.ll_getRefValue(addr, casFeatCode_question);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuestion(int addr, int v) {
        if (featOkTst && casFeat_question == null)
      jcas.throwFeatMissing("question", "edu.cmu.lti.oaqa.watson.types.Query");
    ll_cas.ll_setRefValue(addr, casFeatCode_question, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Query_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_synchronous = jcas.getRequiredFeatureDE(casType, "synchronous", "uima.cas.Boolean", featOkTst);
    casFeatCode_synchronous  = (null == casFeat_synchronous) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_synchronous).getCode();

 
    casFeat_question = jcas.getRequiredFeatureDE(casType, "question", "edu.cmu.lti.oaqa.watson.types.Question", featOkTst);
    casFeatCode_question  = (null == casFeat_question) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_question).getCode();

  }
}



    