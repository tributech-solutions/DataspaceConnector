package de.fraunhofer.isst.dataspaceconnector.unit;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyReader;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class PolicyReaderTest {

    private final PolicyReader policyReader = new PolicyReader();

    private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Test(expected = NullPointerException.class)
    public void getMaxAccess_inputNull_throwNullPointerException() {
        /*ACT*/
        policyReader.getMaxAccess(null);
    }

    @Test
    public void getMaxAccess_inputCorrectOperatorEquals_returnAccessInteger() {
        /*ARRANGE*/
        int maxAccess = 2;

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess), URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        int result = policyReader.getMaxAccess(permission);

        /*ASSERT*/
        Assert.assertEquals(maxAccess, result);
    }

    @Test
    public void getMaxAccess_inputCorrectOperatorLessThanEquals_returnAccessInteger() {
        /*ARRANGE*/
        int maxAccess = 2;

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LTEQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess), URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        int result = policyReader.getMaxAccess(permission);

        /*ASSERT*/
        Assert.assertEquals(maxAccess, result);
    }

    @Test
    public void getMaxAccess_inputCorrectOperatorLessThan_returnAccessInteger() {
        /*ARRANGE*/
        int maxAccess = 2;

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LT)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess), URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        int result = policyReader.getMaxAccess(permission);

        /*ASSERT*/
        Assert.assertEquals(maxAccess - 1, result);
    }

    @Test(expected = Exception.class)
    public void getMaxAccess_inputInvalidWrongConstraintType_throwException() {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.PAY_AMOUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("3", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        policyReader.getMaxAccess(permission);
    }

    @Test
    public void getMaxAccess_inputInvalidAccessBiggerThanMaxInteger_returnSomething() {
        /*ARRANGE*/
        int maxAccess = Integer.MAX_VALUE + 1;

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess), URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        int result = policyReader.getMaxAccess(permission);

        /*ASSERT*/
        Assert.assertNotEquals(maxAccess, result);
        Assert.assertTrue(result >= 0);
    }

    @Test(expected = NumberFormatException.class)
    public void getMaxAccess_inputInvalidAccessNotInteger_throwNumberFormatException() {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("I am not an integer.", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        policyReader.getMaxAccess(permission);
    }

    @Test
    public void getMaxAccess_inputInvalidAccessNegative_returnZero() {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("-3", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        int result = policyReader.getMaxAccess(permission);

        /*ASSERT*/
        Assert.assertEquals(0, result);
    }

    //TODO throws NumberFormatException
    @Test
    public void getMaxAccess_inputInvalidMaxAccessConstraintNotFirstInList_throwNumberFormatException() {
        /*ARRANGE*/
        int maxAccess = 3;

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("P6M", URI.create("xsd:duration")))
                .build();
        Constraint constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess), URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint1);
        constraints.add(constraint2);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        int result = policyReader.getMaxAccess(permission);
        Assert.assertEquals(maxAccess, result);
    }

    @Test(expected = NullPointerException.class)
    public void getTimeInterval_inputNull_throwNullPointerException() {
        /*ACT*/
        policyReader.getTimeInterval(null);
    }

    @Test
    public void getTimeInterval_inputCorrect_returnTimeInterval() throws ParseException {
        /*ARRANGE*/
        String startDate = "2021-01-01T00:00:00Z";
        String endDate = "2022-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        Constraint endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.BEFORE)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(startConstraint);
        constraints.add(endConstraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

        /*ASSERT*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Assert.assertEquals(simpleDateFormat.parse(startDate), timeInterval.getStart());
        Assert.assertEquals(simpleDateFormat.parse(endDate), timeInterval.getEnd());
    }

    @Test(expected = Exception.class)
    public void getTimeInterval_inputInvalidWrongConstraintType_throwException() {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("P6M", URI.create("xsd:duration")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

//        /*ASSERT*/
//        Assert.assertNull(timeInterval.getStart());
//        Assert.assertNull(timeInterval.getEnd());
    }

    @Test(expected = Exception.class)
    public void getTimeInterval_inputInvalidNoStartDate_throwException() throws ParseException {
        /*ARRANGE*/
        String endDate = "2022-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.BEFORE)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

//        /*ASSERT*/
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
//        Assert.assertNull(timeInterval.getStart());
//        Assert.assertEquals(simpleDateFormat.parse(endDate), timeInterval.getEnd());
    }

    @Test(expected = Exception.class)
    public void getTimeInterval_inputInvalidNoEndDate_throwException() throws ParseException {
        /*ARRANGE*/
        String startDate = "2021-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

//        /*ASSERT*/
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
//        Assert.assertEquals(simpleDateFormat.parse(startDate), timeInterval.getStart());
//        Assert.assertNull(timeInterval.getEnd());
    }

    @Test(expected = Exception.class)
    public void getTimeInterval_inputInvalidStartAfterEnd_throwException() throws ParseException {
        /*ARRANGE*/
        String startDate = "2022-01-01T00:00:00Z";
        String endDate = "2021-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        Constraint endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.BEFORE)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(startConstraint);
        constraints.add(endConstraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

//        /*ASSERT*/
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
//        Assert.assertEquals(simpleDateFormat.parse(startDate), timeInterval.getStart());
//        Assert.assertEquals(simpleDateFormat.parse(endDate), timeInterval.getEnd());
    }

    @Test(expected = Exception.class)
    public void getTimeInterval_inputInvalidWrongOperator_throwException() {
        /*ARRANGE*/
        String startDate = "2021-01-01T00:00:00Z";
        String endDate = "2022-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        Constraint endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(startConstraint);
        constraints.add(endConstraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

//        /*ASSERT*/
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
//        Assert.assertNull(timeInterval.getStart());
//        Assert.assertNull(timeInterval.getEnd());
    }

    @Test(expected = Exception.class)
    public void getTimeInterval_inputInvalidWrongDateFormat_throwException() {
        /*ARRANGE*/
        String startDate = "2021-01-01T00:00:00.000";
        String endDate = "2022-01-01T00:00:00.000";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        Constraint endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(startConstraint);
        constraints.add(endConstraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        PolicyReader.TimeInterval timeInterval = policyReader.getTimeInterval(permission);

//        /*ASSERT*/
//        Assert.assertNull(timeInterval.getStart());
//        Assert.assertNull(timeInterval.getEnd());
    }

    @Test(expected = NullPointerException.class)
    public void getEndpoint_inputNull_throwNullPointerException() {
        /*ACT*/
        policyReader.getEndpoint(null);
    }

    //TODO fails with correct input
    @Test
    public void getEndpoint_inputCorrect_returnEndpoint() {
        /*ARRANGE*/
        String endpoint = "https://localhost:8000/notify";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ENDPOINT)
                ._operator_(BinaryOperator.DEFINES_AS)
                ._rightOperand_(new RdfResource(endpoint, URI.create("xsd:anyURI")))
                .build();
        constraints.add(constraint);

        Duty duty = new DutyBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.NOTIFY)))
                ._constraint_(constraints)
                .build();

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._postDuty_(new ArrayList<>(Collections.singletonList(duty)))
                .build();

        /*ACT*/
        String result = policyReader.getEndpoint(permission);

        /*ASSERT*/
        Assert.assertEquals(endpoint, result);
    }

    @Test(expected = Exception.class)
    public void getEndpoint_inputInvalidWrongConstraintType_throwException() {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Duty duty = new DutyBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.NOTIFY)))
                ._constraint_(constraints)
                .build();

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._postDuty_(new ArrayList<>(Collections.singletonList(duty)))
                .build();

        /*ACT*/
        policyReader.getEndpoint(permission);
    }

    @Test
    public void getEndpoint_inputInvalidNotificationConstraintNotFirstInList_throwException() {
        /*ARRANGE*/
        String endpoint = "https://localhost:8000/notify";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        Constraint constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ENDPOINT)
                ._operator_(BinaryOperator.DEFINES_AS)
                ._rightOperand_(new RdfResource(endpoint, URI.create("xsd:anyURI")))
                .build();
        constraints.add(constraint1);
        constraints.add(constraint2);

        Duty duty = new DutyBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.NOTIFY)))
                ._constraint_(constraints)
                .build();

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._postDuty_(new ArrayList<>(Collections.singletonList(duty)))
                .build();

        /*ACT*/
        String result = policyReader.getEndpoint(permission);

        /*ASSERT*/
        Assert.assertEquals(endpoint, result);
    }

    @Test(expected = NullPointerException.class)
    public void getPipEndpoint_inputNull_throwNullPointerException() {
        /*ACT*/
        policyReader.getPipEndpoint(null);
    }

    @Test
    public void getPipEndpoint_inputCorrect_returnPipEndpoint() {
        /*ARRANGE*/
        URI pipEndpoint = URI.create("https://pip.com");

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                ._pipEndpoint_(pipEndpoint)
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        URI result = policyReader.getPipEndpoint(permission);

        /*ASSERT*/
        Assert.assertEquals(pipEndpoint, result);
    }

    @Test(expected = Exception.class)
    public void getPipEndpoint_inputInvalidConstraintHasNoPipEndpoint_returnNull() {
        /*ARRANGE*/
        URI pipEndpoint = URI.create("https://pip.com");

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        URI result = policyReader.getPipEndpoint(permission);

//        /*ASSERT*/
//        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void getDate_inputNull_throwNullPointerException() throws ParseException {
        /*ACT*/
        policyReader.getDate(null);
    }

    @Test
    public void getDate_inputCorrect_returnDate() throws ParseException {
        /*ARRANGE*/
        String date = "2021-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(date, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        Date result = policyReader.getDate(permission);

        /*ASSERT*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Assert.assertEquals(simpleDateFormat.parse(date), result);

    }

    @Test(expected = ParseException.class)
    public void getDate_inputInvalidWrongConstraintType_throwException() throws ParseException {
        /*ARRANGE*/
        String date = "2021-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        policyReader.getDate(permission);
    }

    @Test(expected = ParseException.class)
    public void getDate_inputInvalidWrongDateFormat_throwException() throws ParseException {
        /*ARRANGE*/
        String date = "2021-01-01T00:00:00.000";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(date, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        policyReader.getDate(permission);
    }

    @Test(expected = ParseException.class)
    public void getDate_inputInvalidNotADate_throwException() throws ParseException {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource("I am not a date.", URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        policyReader.getDate(permission);
    }

    @Test
    public void getDate_inputInvalidDateConstraintNotFirstInList_throwException() throws ParseException {
        /*ARRANGE*/
        String date = "2021-01-01T00:00:00Z";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("P6M", URI.create("xsd:duration")))
                .build();
        Constraint constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(date, URI.create("xsd:dateTimeStamp")))
                .build();
        constraints.add(constraint1);
        constraints.add(constraint2);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        Date result = policyReader.getDate(permission);

        /*ASSERT*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Assert.assertEquals(simpleDateFormat.parse(date), result);
    }

    @Test(expected = NullPointerException.class)
    public void getDuration_inputNull_throwNullPointerException() throws DatatypeConfigurationException {
        policyReader.getDuration(null);
    }

    @Test
    public void getDuration_inputCorrect_returnDuration() throws DatatypeConfigurationException {
        /*ARRANGE*/
        String duration = "P6M";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource(duration, URI.create("xsd:duration")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        Duration result = policyReader.getDuration(permission);

        /*ASSERT*/
        Assert.assertEquals(DatatypeFactory.newInstance().newDuration(duration), result);
    }

    @Test(expected = Exception.class)
    public void getDuration_inputInvalidWrongConstraintType_throwException() throws DatatypeConfigurationException {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LTEQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        Duration result = policyReader.getDuration(permission);

//        /*ASSERT*/
//        Assert.assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDuration_inputInvalidNotADuration_throwException() throws DatatypeConfigurationException {
        /*ARRANGE*/
        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("I am not a duration.", URI.create("xsd:duration")))
                .build();
        constraints.add(constraint);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        policyReader.getDuration(permission);
    }

    @Test
    public void getDuration_inputInvalidDurationConstraintNotFirstInList_throwException()
            throws DatatypeConfigurationException {
        /*ARRANGE*/
        String duration = "P6M";

        ArrayList<Constraint> constraints = new ArrayList<>();
        Constraint constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LTEQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        Constraint constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource(duration, URI.create("xsd:duration")))
                .build();
        constraints.add(constraint1);
        constraints.add(constraint2);

        Permission permission = new PermissionBuilder()
                ._action_(new ArrayList<>(Collections.singletonList(Action.USE)))
                ._constraint_(constraints)
                .build();

        /*ACT*/
        Duration result = policyReader.getDuration(permission);

        /*ASSERT*/
        Assert.assertNotNull(result);
    }

}
