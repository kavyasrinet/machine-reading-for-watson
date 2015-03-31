

/* First created by JCasGen Sun Apr 27 16:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.watson.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sun Apr 27 16:40:19 EDT 2014
 * XML source: /home/diwang/Dropbox/oaqa-workspace/watson-cloud/src/main/resources/WatsonCloudTypeSystem.xml
 * @generated */
public class Query extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Query.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Query() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Query(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Query(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: synchronous

  /** getter for synchronous - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getSynchronous() {
    if (Query_Type.featOkTst && ((Query_Type)jcasType).casFeat_synchronous == null)
      jcasType.jcas.throwFeatMissing("synchronous", "edu.cmu.lti.oaqa.watson.types.Query");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Query_Type)jcasType).casFeatCode_synchronous);}
    
  /** setter for synchronous - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSynchronous(boolean v) {
    if (Query_Type.featOkTst && ((Query_Type)jcasType).casFeat_synchronous == null)
      jcasType.jcas.throwFeatMissing("synchronous", "edu.cmu.lti.oaqa.watson.types.Query");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Query_Type)jcasType).casFeatCode_synchronous, v);}    
   
    
  //*--------------*
  //* Feature: question

  /** getter for question - gets 
   * @generated
   * @return value of the feature 
   */
  public Question getQuestion() {
    if (Query_Type.featOkTst && ((Query_Type)jcasType).casFeat_question == null)
      jcasType.jcas.throwFeatMissing("question", "edu.cmu.lti.oaqa.watson.types.Query");
    return (Question)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Query_Type)jcasType).casFeatCode_question)));}
    
  /** setter for question - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuestion(Question v) {
    if (Query_Type.featOkTst && ((Query_Type)jcasType).casFeat_question == null)
      jcasType.jcas.throwFeatMissing("question", "edu.cmu.lti.oaqa.watson.types.Query");
    jcasType.ll_cas.ll_setRefValue(addr, ((Query_Type)jcasType).casFeatCode_question, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    