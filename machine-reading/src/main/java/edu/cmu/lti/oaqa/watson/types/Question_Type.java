
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
public class Question_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Question_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Question_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Question(addr, Question_Type.this);
  			   Question_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Question(addr, Question_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Question.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.watson.types.Question");
 
  /** @generated */
  final Feature casFeat_questionText;
  /** @generated */
  final int     casFeatCode_questionText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getQuestionText(int addr) {
        if (featOkTst && casFeat_questionText == null)
      jcas.throwFeatMissing("questionText", "edu.cmu.lti.oaqa.watson.types.Question");
    return ll_cas.ll_getStringValue(addr, casFeatCode_questionText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuestionText(int addr, String v) {
        if (featOkTst && casFeat_questionText == null)
      jcas.throwFeatMissing("questionText", "edu.cmu.lti.oaqa.watson.types.Question");
    ll_cas.ll_setStringValue(addr, casFeatCode_questionText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_items;
  /** @generated */
  final int     casFeatCode_items;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getItems(int addr) {
        if (featOkTst && casFeat_items == null)
      jcas.throwFeatMissing("items", "edu.cmu.lti.oaqa.watson.types.Question");
    return ll_cas.ll_getIntValue(addr, casFeatCode_items);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setItems(int addr, int v) {
        if (featOkTst && casFeat_items == null)
      jcas.throwFeatMissing("items", "edu.cmu.lti.oaqa.watson.types.Question");
    ll_cas.ll_setIntValue(addr, casFeatCode_items, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Question_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_questionText = jcas.getRequiredFeatureDE(casType, "questionText", "uima.cas.String", featOkTst);
    casFeatCode_questionText  = (null == casFeat_questionText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_questionText).getCode();

 
    casFeat_items = jcas.getRequiredFeatureDE(casType, "items", "uima.cas.Integer", featOkTst);
    casFeatCode_items  = (null == casFeat_items) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_items).getCode();

  }
}



    