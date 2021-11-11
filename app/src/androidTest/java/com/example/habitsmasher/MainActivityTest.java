package com.example.habitsmasher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class MainActivityTest {
    private static final String HABIT_TITLE_FIELD = "Habit title";
    private static final String HABIT_REASON_FIELD = "Habit reason";
    private static final String HABIT_LIST_TEXT = "Habit List";
    private static final String HABIT_EVENT_COMMENT_FIELD = "Enter Comment...";
    private static final String WRONG_ACTIVITY_MESSAGE = "Wrong Activity";
    private static final String PROFILE_TEXT = "Profile";
    private static final String HOME_TEXT = "Home";
    private static final boolean PUBLIC_HABIT = true;
    private static final boolean PRIVATE_HABIT = false;
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String HABIT_TITLE_ERROR_MESSAGE = "Incorrect habit title entered";
    private static final String HABIT_REASON_ERROR_MESSAGE = "Incorrect habit reason entered";
    private static final String EMPTY_DATE_ERROR_MESSAGE = "Please enter a start date";
    private static final String NO_DAYS_SELECTED_ERROR_MESSAGE = "Please select a weekly schedule.";
    private static final String HABIT_EVENT_COMMENT_ERROR_MESSAGE = "Incorrect habit event comment entered";
    private static final String EDIT_BUTTON = "EDIT";
    private static final HabitEventList EMPTY_HABIT_EVENT_LIST = new HabitEventList();
    private static final String DELETE_BUTTON = "DELETE";
    private static final String EDIT_HABIT_DIALOG = "Edit Habit";
    private static final long HABIT_ID = 1;
    private static final String VALID_EMAIL = "validEmail@gmail.com";
    private static final String VALID_USERNAME = "validUsername";
    private static final String VALID_PASSWORD = "validPwd";
    private static final String NEW_USER_ID = "123456789";
    private static final String SIGN_UP_TEXT = "Sign Up";
    private static final String INVALID_EMAIL = "badEmail123";
    private static final String USERNAME_IS_REQUIRED = "Username is required!";
    private static final String EMAIL_IS_REQUIRED = "Email is required!";
    private static final String PASSWORD_IS_REQUIRED = "Password is required!";
    private static final String INVALID_EMAIL_FORMAT = "Invalid email format!";
    private static final String INCORRECT_EMAIL_PASSWORD = "Incorrect email/password";
    private static final String LOGIN_TEXT = "Login";
    private static final String TEST_USER_ID = "TEST";
    private static final String TEST_USER_USERNAME = "TestUser";
    private static final String TEST_USER_EMAIL = "test@gmail.com";
    private static final String TEST_USER_PASSWORD = "123456";

    private Solo _solo;
    private User _testUser = new User(TEST_USER_ID, TEST_USER_USERNAME, TEST_USER_EMAIL,
                                      TEST_USER_PASSWORD);

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        _solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Navigate to the habit list using the bottom navigation
     */
    @Test
    public void navigateToHabitList(){
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // ensure that the app has transitioned to the Habit List screen
        assertTextOnScreen(HABIT_LIST_TEXT);
    }

    /**
     * Navigate to the profile page using the bottom navigation
     */
    @Test
    public void navigateToUserProfile(){
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Notifications tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_notifications));

        // ensure that the app has transitioned to the Notifications screen
        assertTextOnScreen(PROFILE_TEXT);
    }

    @Test
    public void navigateToHabitViewFragment() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("viewHabitTest", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Click on added Habit
        _solo.clickOnText(testHabit.getTitle());

        // Check the title is correct
        assertTextOnScreen(testHabit.getTitle());

        // Check that reason is correct
        assertTextOnScreen(testHabit.getReason());

        // Check that the date is correct
        assertTextOnScreen(new SimpleDateFormat(DATE_PATTERN).format(testHabit.getDate()));

        // Check that the days are correct
        assertCorrectDays();

        //check the privacy is right
        assertCorrectPrivacy(testHabit.getPublic(), PUBLIC_HABIT);

        // Click up/back button
        _solo.goBack();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAddedSuccessfully() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("addHabitSuccessTest", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAdditionFails_emptyTitle() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("addHabitEmptyTitle", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(HABIT_TITLE_ERROR_MESSAGE);

        // Add habit title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAdditionFails_titleTooLong() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("ExampleHabitTitleThatIsTooLong", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(HABIT_TITLE_ERROR_MESSAGE);

        // Add shorter habit title
        _solo.clearEditText(_solo.getEditText(testHabit.getTitle()));
        testHabit.setTitle("shorterTitle");
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAdditionFails_reasonTooLong() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("addHabitReasonLong", "AnExampleHabitReasonThatIsTooLong", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(HABIT_REASON_ERROR_MESSAGE);

        // Add shorter habit title
        _solo.clearEditText(_solo.getEditText(testHabit.getReason()));
        testHabit.setReason("shorterReason");
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAdditionFails_reasonEmpty() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("addHabitEmptyReason", "", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(HABIT_REASON_ERROR_MESSAGE);

        // Add habit reason
        testHabit.setReason("acceptable reason");
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAdditionFails_dateEmpty() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("addHabitEmptyDate", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(EMPTY_DATE_ERROR_MESSAGE);

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitAdditionFails_NoDays(){
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("addHabitNoDays", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check for toast message
        checkForToastMessage(NO_DAYS_SELECTED_ERROR_MESSAGE);

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();


        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);

    }

    @Test
    public void ensurePublicHabitAdditionCorrect(){
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        _solo.waitForActivity("ProfileActivity", 4000);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create public test habit
        Habit testHabit = new Habit("publicHabitCheck", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Select Public (public by default, leaving single fragment for consistancy
        // setPrivacyInAddHabitDialogBox(PUBLIC_HABIT);

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        //get the habit we just made
        _solo.clickOnText(testHabit.getTitle());

        // assert that the correct buttons are selected, not just the underlying habit
        CheckBox publicBox = (CheckBox) _solo.getView(R.id.public_button);
        CheckBox privateBox = (CheckBox) _solo.getView(R.id.private_button);
        assert publicBox.isChecked();
        assert !(privateBox.isChecked());

        _solo.goBack();

        deleteTestHabit(testHabit);
    }

    @Test
    public void ensurePrivateHabitAdditionCorrect(){
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        _solo.waitForActivity("ProfileActivity", 4000);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create public test habit
        Habit testHabit = new Habit("privateHabitCheck", "Test Reason", new Date(), "MO WE FR", PRIVATE_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        //get the habit we just made
        _solo.clickOnText(testHabit.getTitle());

        //get the checkboxes to check
        CheckBox publicBox = (CheckBox) _solo.getView(R.id.public_button);
        CheckBox privateBox = (CheckBox) _solo.getView(R.id.private_button);

        // assert that the correct buttons are selected, not just the underlying habit
        assert privateBox.isChecked();
        assert !(publicBox.isChecked());

        deleteTestHabit(testHabit);
    }

    /**
     * Tests edit function is working correctly
     * Testing swipe code found here:
     * URL: https://stackoverflow.com/questions/24664730/writing-a-robotium-test-to-swipe-open-an-item-on-a-swipeable-listview
     * Name: C0D3LIC1OU5
     * Date: July 9, 2014
     */
    @Test
    public void ensureEditIsFunctioning() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("editHabitTest", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        swipeLeftOnHabit(testHabit);
        _solo.waitForView(R.id.edit_button);
        _solo.clickOnButton(EDIT_BUTTON);

        // wait for edit habit dialog to spawn after edit is clicked
        _solo.waitForText(EDIT_HABIT_DIALOG, 1, 5000);

        // clear Edit Text fields
        _solo.clearEditText(0);
        _solo.clearEditText(1);

        Habit testEditHabit = new Habit("editHabitWorked", "testReason1", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);
        // enter new values
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testEditHabit.getTitle());
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testEditHabit.getReason());
        enterCurrentDateInAddHabitDialogBox();
        clickConfirmButtonInAddHabitDialogBox();
        assertTextOnScreen(HABIT_LIST_TEXT);
        assertTextOnScreen(testEditHabit.getTitle());

        deleteTestHabit(testEditHabit);
    }

    @Test
    public void ensureHabitDeletedSuccessfully() {
        logInTestUser();

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit("deleteHabitTest", "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Check that the current fragment is the habit list
        assertTextOnScreen(HABIT_LIST_TEXT);

        // Ensure added Habit is present in the list
        assertTextOnScreen(testHabit.getTitle());

        deleteTestHabit(testHabit);
    }

    @Test
    public void navigateToHabitEventsList() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("ViewEventsTest");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View addHabitEventButton = _solo.getView(R.id.add_habit_event_fab);
        assertNotNull(addHabitEventButton);

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventAddedSuccessfully() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("AddEventTest");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment", UUID.randomUUID().toString());

        // Enter comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventFails_emptyComment() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("EmptyComEventTest");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment", UUID.randomUUID().toString());

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(HABIT_EVENT_COMMENT_ERROR_MESSAGE);

        // Add habit comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventFails_commentTooLong() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("LongCommentEventTest");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment that is too long, and too many characters", UUID.randomUUID().toString());

        // Enter comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(HABIT_EVENT_COMMENT_ERROR_MESSAGE);

        // Change habit comment
        _solo.clearEditText(_solo.getEditText(testEvent.getComment()));
        testEvent.setComment("ShorterComment");
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventFails_dateEmpty() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("EmptyDateEventTest");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment", UUID.randomUUID().toString());

        // Enter comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that the error message is displayed
        checkForToastMessage(EMPTY_DATE_ERROR_MESSAGE);

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventDeletedSuccessfully() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("DeleteEventTest");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment", UUID.randomUUID().toString());

        // Enter comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Test deleting the habit event
        deleteTestHabitEvent(testEvent);

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventEditedSuccessfully_changeDate() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("EditDateEvent");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment", UUID.randomUUID().toString());

        // Enter comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Click on edit button
        swipeLeftOnHabitEvent(testEvent);
        _solo.waitForView(R.id.edit_habit_event_button);
        _solo.clickOnButton(EDIT_BUTTON);

        // Check that dialog opens
        assertTextOnScreen("Edit Habit Event");
        enterCurrentDateInAddHabitEventDialogBox();
        clickConfirmButtonInAddHabitEventDialogBox();

        assertTextOnScreen(testEvent.getComment());

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void ensureHabitEventEditedSuccessfully_changeComment() {
        logInTestUser();

        // Navigate to view habit
        Habit testHabit = goToViewHabit("EditCommentEvent");

        // Click on history button
        _solo.clickOnView(_solo.getView(R.id.eventHistoryButton));

        // Check that habit event list fragment is displaying
        View fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Click on add habit event floating action button to add habit event
        _solo.clickOnView(_solo.getView(R.id.add_habit_event_fab));

        // Create test habit event
        HabitEvent testEvent = new HabitEvent(new Date(), "Test Comment", UUID.randomUUID().toString());

        // Enter comment
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, testEvent.getComment());

        // Enter date
        enterCurrentDateInAddHabitEventDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that habit event list fragment is displaying
        fragment = _solo.getView(R.id.habit_event_list_container);
        assertNotNull(fragment);

        // Ensure added habit event is present in the list
        assertTextOnScreen(testEvent.getComment());

        // Click on edit button
        swipeLeftOnHabitEvent(testEvent);
        _solo.waitForView(R.id.edit_habit_event_button);
        _solo.clickOnButton(EDIT_BUTTON);

        // Check that dialog opens
        assertTextOnScreen("Edit Habit Event");

        // Change comment
        _solo.clearEditText(_solo.getEditText(testEvent.getComment()));
        setFieldInAddHabitDialogBox(HABIT_EVENT_COMMENT_FIELD, "New comment!");
        clickConfirmButtonInAddHabitEventDialogBox();

        // Check that new comment is displayed
        assertTextOnScreen("New comment!");

        // Go back and delete habit
        _solo.goBack();
        _solo.goBack();
        deleteTestHabit(testHabit);
    }

    @Test
    public void signUpNewUser_emptyUsername_signUpFails() {
        User newUser = new User(NEW_USER_ID, "", VALID_EMAIL, VALID_PASSWORD);

        _solo.clickOnButton(SIGN_UP_TEXT);

        _solo.enterText(_solo.getEditText("Username"), newUser.getUsername());
        _solo.enterText(_solo.getEditText("Email"), newUser.getEmail());
        _solo.enterText(_solo.getEditText("Password"), newUser.getPassword());

        _solo.clickOnButton(SIGN_UP_TEXT);

        assertTextOnScreen(USERNAME_IS_REQUIRED);
    }

    @Test
    public void signUpNewUser_emptyEmail_signUpFails() {
        User newUser = new User(NEW_USER_ID, VALID_USERNAME, "", VALID_PASSWORD);

        _solo.clickOnButton(SIGN_UP_TEXT);

        _solo.enterText(_solo.getEditText("Username"), newUser.getUsername());
        _solo.enterText(_solo.getEditText("Email"), newUser.getEmail());
        _solo.enterText(_solo.getEditText("Password"), newUser.getPassword());

        _solo.clickOnButton(SIGN_UP_TEXT);

        assertTextOnScreen(EMAIL_IS_REQUIRED);
    }

    @Test
    public void signUpNewUser_emptyPassword_signUpFails() {
        User newUser = new User(NEW_USER_ID, VALID_USERNAME, VALID_EMAIL, "");

        _solo.clickOnButton(SIGN_UP_TEXT);

        _solo.enterText(_solo.getEditText("Username"), newUser.getUsername());
        _solo.enterText(_solo.getEditText("Email"), newUser.getEmail());
        _solo.enterText(_solo.getEditText("Password"), newUser.getPassword());

        _solo.clickOnButton(SIGN_UP_TEXT);

        assertTextOnScreen(PASSWORD_IS_REQUIRED);
    }

    @Test
    public void loginTestUser_invalidEmail_loginFails() {
        _solo.enterText(_solo.getEditText("Email"), INVALID_EMAIL);
        _solo.enterText(_solo.getEditText("Password"), _testUser.getPassword());

        _solo.clickOnButton(LOGIN_TEXT);

        assertTextOnScreen(INVALID_EMAIL_FORMAT);
    }

    @Test
    public void loginTestUser_emptyEmail_loginFails() {
        _solo.enterText(_solo.getEditText("Email"), "");
        _solo.enterText(_solo.getEditText("Password"), _testUser.getPassword());

        _solo.clickOnButton(LOGIN_TEXT);

        assertTextOnScreen(EMAIL_IS_REQUIRED);
    }

    @Test
    public void loginTestUser_invalidPassword_loginFails() {
        _solo.enterText(_solo.getEditText("Email"), _testUser.getEmail());
        _solo.enterText(_solo.getEditText("Password"), "wrongPassword");

        _solo.clickOnButton(LOGIN_TEXT);

        assertTextOnScreen(INCORRECT_EMAIL_PASSWORD);
    }

    @Test
    public void loginTestUser_emptyPassword_loginFails() {
        _solo.enterText(_solo.getEditText("Email"), _testUser.getEmail());
        _solo.enterText(_solo.getEditText("Password"), "");

        _solo.clickOnButton(LOGIN_TEXT);

        assertTextOnScreen(PASSWORD_IS_REQUIRED);
    }

    private void deleteTestHabit(Habit habitToDelete) {
        swipeLeftOnHabit(habitToDelete);
        _solo.waitForView(R.id.delete_button);
        _solo.clickOnButton(DELETE_BUTTON);

        assertFalse(isTextOnScreen(habitToDelete.getTitle()));
    }

    private void deleteTestHabitEvent(HabitEvent eventToDelete) {
        swipeLeftOnHabitEvent(eventToDelete);


        _solo.waitForView(R.id.delete_habit_event_button);


        _solo.clickOnButton(DELETE_BUTTON);

        assertFalse(isTextOnScreen(eventToDelete.getComment()));
    }

    private void swipeLeftOnHabit(Habit habitToDelete) {
        TextView view = _solo.getText(habitToDelete.getTitle());

        // locate row
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        // larger padding from righthand side of screen to ensure swipe functions
        int displayWidth = _solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
        int fromX = displayWidth - 100;
        int fromY = location[1];

        // 0 so Robotium swipes to leftmost side of screen
        _solo.drag(fromX, 0, fromY, fromY, 10);
    }

    private void swipeLeftOnHabitEvent(HabitEvent eventToDelete) {
        TextView view = _solo.getText(eventToDelete.getComment());

        // locate row
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int displayWidth = _solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();

        int fromX = displayWidth - 10;
        int fromY = location[1];
        _solo.drag(location[0], 0, fromY, fromY, 10);
        _solo.drag(location[0], 0, fromY, fromY, 10);
    }


    private Habit goToViewHabit(String testName) {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        _solo.assertCurrentActivity(WRONG_ACTIVITY_MESSAGE, MainActivity.class);

        // click on the Habit List tab in the bottom navigation bar
        _solo.clickOnView(_solo.getView(R.id.navigation_dashboard));

        // click on add habit floating action button to add habit
        _solo.clickOnView(_solo.getView(R.id.add_habit_fab));

        // Create test habit
        Habit testHabit = new Habit(testName, "Test Reason", new Date(), "MO WE FR", PUBLIC_HABIT, HABIT_ID, EMPTY_HABIT_EVENT_LIST);

        // Enter title
        setFieldInAddHabitDialogBox(HABIT_TITLE_FIELD, testHabit.getTitle());

        // Enter reason
        setFieldInAddHabitDialogBox(HABIT_REASON_FIELD, testHabit.getReason());

        // Enter date
        enterCurrentDateInAddHabitDialogBox();

        // Select days
        setDaysInAddHabitDialogBox();

        // Click confirm
        clickConfirmButtonInAddHabitDialogBox();

        // Click on added Habit
        _solo.clickOnText(testHabit.getTitle());

        return testHabit;
    }

    private void clickConfirmButtonInAddHabitDialogBox() {
        _solo.clickOnView(_solo.getView(R.id.confirm_habit));
    }

    private void clickConfirmButtonInAddHabitEventDialogBox() {
        _solo.clickOnView(_solo.getView(R.id.confirm_habit_event));
    }

    private void enterCurrentDateInAddHabitDialogBox() {
        _solo.clickOnView(_solo.getView(R.id.habit_date_selection));
        _solo.clickOnText("OK");
    }

    private void enterCurrentDateInAddHabitEventDialogBox() {
        _solo.clickOnView(_solo.getView(R.id.habit_event_date_selection));
        _solo.clickOnText("OK");
    }

    private void setFieldInAddHabitDialogBox(String fieldToSet, String text) {
        _solo.enterText(_solo.getEditText(fieldToSet), text);
    }

    private void setDaysInAddHabitDialogBox(){
        _solo.clickOnView(_solo.getView(R.id.monday_button));
        _solo.clickOnView(_solo.getView(R.id.wednesday_button));
        _solo.clickOnView(_solo.getView(R.id.friday_button));
    }

    private void setPrivacyInAddHabitDialogBox(boolean privacy){
        if (privacy == PUBLIC_HABIT) {
            _solo.clickOnView(_solo.getView(R.id.public_button));
        } else {
            _solo.clickOnView(_solo.getView(R.id.private_button));
        }
    }

    private void checkForToastMessage(String errorMessage) {
        assertTrue(_solo.searchText(errorMessage));
    }

    private void assertTextOnScreen(String text) {
        assertTrue(isTextOnScreen(text));
    }

    private boolean isTextOnScreen(String text) {
        return _solo.waitForText(text, 1, 2000);
    }

    private void assertCorrectDays(){
        // I'm not entirely sure how to check the state of a button nor get the drawable without context, so
        // my workaround checks the drawable of the buttons compared to each other, which change based on whether they're checked.
        assertNotEquals(_solo.getView(R.id.tuesday_button).getBackground(),_solo.getView(R.id.monday_button).getBackground());
        assertNotEquals(_solo.getView(R.id.wednesday_button).getBackground(), _solo.getView(R.id.thursday_button).getBackground());
        assertNotEquals(_solo.getView(R.id.friday_button).getBackground(), _solo.getView(R.id.saturday_button).getBackground());
        assertNotEquals(_solo.getView(R.id.sunday_button).getBackground(), _solo.getView(R.id.monday_button).getBackground());

        // If you include these lines, you'll see that each button has the same background but different hex code, meaning
        // Different states. This gives the difference between states.
        //Log.d("Monday Background", _solo.getView(R.id.monday_button).getBackground().toString());
        //Log.d("Tuesday Background", _solo.getView(R.id.tuesday_button).getBackground().toString());
    }

    private void assertCorrectPrivacy(boolean privacy, boolean expected_Privacy){
        //assert the habit is correctly labeled as private or public
        assertEquals(privacy, expected_Privacy);
    }

    private void logInTestUser() {
        _solo.enterText(_solo.getEditText("Email"), _testUser.getEmail());
        _solo.enterText(_solo.getEditText("Password"), _testUser.getPassword());

        _solo.clickOnButton(LOGIN_TEXT);
    }
}
