'use strict';
define([], function() {

	return {
    INDEX_WELCOME: 'Welcome to Moovify!',
	  INDEX_SLOGAN: 'The movie is over?',
    INDEX_MY_FEED: 'My Feed',
    HOTTEST_USERS: 'Hottest users',
    ADMIN_TITLE: 'Admin',
    FOLLOW_TITLE: 'Follow',
    BOOKMARKED_TITLE:'Bookmarked',
    ADMIN_PANEL_BTN: 'Admin panel',
    USER_LOGIN_TITLE: 'Moovify | Login',
    USER_LOGIN: 'Log in',
    USER_LOGIN_ALT: 'Login',
    USER_LOGOUT: 'Logout',
    USER_LOGIN_USERNAME: 'Username',
    USER_LOGIN_PASSWORD: 'Password',
    USER_LOGIN_REMEMBERME: 'Remember me',
    USER_LOGIN_NOACCOUNT: 'Do not have an account yet?',
    USER_LOGIN_RESETPASSWORD: 'Forgot my password',
    USER_CREATE_SIGNUP: 'Sign up',
    USER_CREATE_SIGNUP_ALT: 'Signup',
    USER_CREATE_SIGNUPDESC: 'Please fill this form to get free access to Moovify',
    USER_CREATE_NAME: 'Name',
    USER_CREATE_EMAIL: 'Email',
    USER_CREATE_USERNAME: 'Username',
    USER_CREATE_PASSWORD: 'Password',
    USER_CREATE_REPEATPASSWORD: 'Repeat password',
    USER_CREATE_SELECTAVATAR: 'Want to select an avatar? (Optional)',
    USER_CREATE_SELECTFILE: 'Select file',
    USER_CREATE_NOAVATARSELECTED: 'No Avatar Selected',
    USER_CREATE_AVATARSELECTED: 'Selected Avatar:',
    USER_CREATE_BUTTON: 'Create account',
    USER_CREATE_ALREADYACCOUNT: 'Already have an account?',
    USER_PROFILE: 'My profile',
    USER_PROFILE_CREATION_DATE: 'In Moovify since:',
    USER_EMAILCONFIRM_TITLE: 'Email confirmation requiered',
    USER_EMAILCONFIRM_TEXT: 'To be able to create a new post, you have to confirm your email address. Check your email box, we sent you a link!',
    USER_PROFILE_RESENDEMAIL: 'Resend email',
    USER_EMAILCONFIRM_CLOSEMODAL: 'Ok, got it!',
    USER_NAME:'Name: {{name}}',
    USER_DESCRIPTION: 'Description: {{description}}',
    USER_DESCRIPTION_EMPTY: 'Description: This user does not have a description yet',
    USER_DESCRIPTION_PLACEHOLDER: 'Description',
    USER_VOTES: 'Total votes: {{votes}}',
    USER_UPDATE_PASSWORD: 'New password',
    USER_UPDATE_PASSWORD_CHECK: 'Repeat new password',
    USER_RESET_PASSWORD_HEADER: "Enter your email to reset your password",
    USER_RESETPASSWORD_VALIDATEDEMAIL: 'Important: your email must have been validated to be able to change your password',
    USER_RESETPASSWORD_SEND: "Send email",
    USER_RESETPASSWORD_EMAIL: "Enter email",
    COMMENT_BY:'By: {{username}}',
    COMMENT_BY_REMOVED: 'This user was removed',
    COMMENT_META: 'Post: {{title}} - Votes: {{votes}}',
    VOTES_DISPLAY:'-Votes: {{votes}}',
    SEARCH_TITLE:'Search',
    SEARCH_PLACEHOLDER: 'Search...',
    ALL_POSTS: 'All posts',
    POST_CATEGORIES: 'Type of post:',
    POST_AGE: 'From:',
    MOVIE_CATEGORY: 'Category:',
    MOVIE_DECADE: 'Decade:',
    USER_ROLE: 'Role:',
    SEARCH_ORDER_BY: 'Order by:',
    POST_TAB_DISPLAY: 'Posts',
    MOVIE_TAB_DISPLAY: 'Movies',
    USER_TAB_DISPLAY: 'Users',
    COMMENT_TAB_DISPLAY: 'Comments',
    MOVIES_DISCUSSED: 'Movies discussed:',
    LAST_HOUR:'An hour ago',
    LAST_DAY:'A day ago',
    LAST_WEEK:'A week ago',
    LAST_MONTH:'A month ago',
    LAST_YEAR: 'A year ago',
    ANY_TIME: 'Any time',
    MOVIE_YEAR: 'Year: {{year}}',
    ALL: 'All',
    ANY: 'Any',
    CATEGORIES: 'Categories:',
    POST_NOT_FOUND: 'There are no posts that match those requirements. Try changing the search parameters or looking for some new posts in the home screen.',
    MOVIES_NOT_FOUND: 'There are no movies that match those requirements. Try changing the search parameters.',
    USERS_NOT_FOUND: 'There are no users that match those requirements. Try changing the search parameters.',
    COMMENT_NOT_FOUND_PROFILE: 'You have no comments yet',
    NAVBAR_CREATE_POST: 'Create post',
    FORM_FIELD_REQUIRED: 'Field required',
    FORM_BAD_CREDENTIALS: 'Incorrect user or password',
    FORM_MIN_LENGTH_ERROR: 'Minimum length is {{ minLen }} characters',
    FORM_MAX_LENGTH_ERROR: 'Maximum length is {{ maxLen }} characters',
    FORM_NAME_PATTERN_ERROR: 'Only letters and spaces are allowed',
    FORM_EMAIL_PATTERN_ERROR: 'Invalid email',
    FORM_USERNAME_PATTERN_ERROR: "Only these characters are allowed: letters, numbers, '_' and '#'",
    FORM_PASSWORD_PATTERN_ERROR: "White spaces are not allowed",
    FORM_REPEAT_PASSWORD_ERROR: "Passwords don't match",
    FORM_DUPLICATED_FIELD_ERROR: "{{ field }} already exists, please choose another",
    FORM_AVATAR_SIZE_ERROR: "Max size 1MB",
    FORM_TITLE_REQUIRED:"You must write a title",
    FORM_CATEGORY_REQUIRED:"You must pick a valid category",
    FORM_BODY_REQUIRED:"Your post must have a body",
    FORM_MOVIE_MIN_REQUIRED:"You must pick at least one movie",
    FORM_MOVIE_MAX_REQUIRED:"You cannot pick more than 20 movies",
    POST_CREATE: "Create new post",
    POST_CREATE_CHOOSE_TITLE: "Choose a title for your post (At least 6 characters)",
    POST_CREATE_TITLE_PLACEHOLDER: "Title",
    POST_CREATE_CHOOSE_CATEGORY: "Select a category for your post (Required)",
    POST_CREATE_CATEGORY_PLACEHOLDER: "Select a category",
    POST_CREATE_WRITE_BODY: "Write the body of your post here (Cannot be empty)",
    POST_CREATE_BODY_PLACEHOLDER: "Write your post here...",
    POST_CREATE_BUTTON: "CREATE",
    POST_CREATE_MOVIES_DISCUSSED: "What movies are discussed in this Post?",
    POST_CREATE_MOVIES_CONSTRAINS: "You have to choose at least one (max 20)",
    POST_CREATE_ADD:"ADD",
    POST_CREATE_MOVIES_PLACEHOLDER: "Insert the name of the movie you want to add",
    POST_CREATE_TAGS: "Add some key words that describe this post",
    POST_CREATE_TAGS_CONSTRAINS: "You may add up to 5 tags (no more than 50 characters)",
    POST_CREATE_TAGS_PLACEHOLDER: "Write the tag here",
    POST_CREATE_SEND: "SEND",
    POST_ORDER_HOTTEST: "Hottest",
    POST_ORDER_NEWEST: "Newest",
    UPDATE_AVATAR_MODAL_TITLE: "Are you sure you want to update your avatar?",
    UPDATE_AVATAR_MODAL_BODY: "File name: ",
    UPDATE_AVATAR_MODAL_CANCEL: "No",
    UPDATE_AVATAR_MODAL_ACCEPT: "Yes",
    UPDATE_INFO_MODAL_CANCEL: "Cancel",
    UPDATE_INFO_MODAL_ACCEPT: "Accept",
    MIN_USER_DISPLAY_META_NAME_VOTES: "Name: {{name}} - Total votes: {{totalVotes}}",
    MOVIE_VIEW_TITLE:'{{title}} ({{year}})',
    MOVIE_POSTER:'{{title}} poster',
    MOVIE_OVERVIEW:'Overview',
    MOVIE_POST_ABOUT:'Posts about this movie:',
    CHANGE_PASSWORD_TITLE: 'Change password',
    RECOVER_POST_DELETED:'Recover deleted posts',
    RECOVER_COMMENT_DELETED:'Recover deleted comments',
    RECOVER_USER_DELETED:'Recover deleted users',
    RESTORE:'Restore',
    MAIL_FEEDBACK_SUCCESS: 'Mail sent successfully, check your mailbox!',
    MAIL_FEEDBACK_ERROR: 'Mail not sent, try again later...',
    UPDATE_PASS_TITLE: 'Reset your password',
    UPDATE_PASS_MSG: 'Insert here your new password',
    UPDATE_PASS_BTN: 'Change password',
    UPDATE_PASS_TOKEN: 'Token',
    UPDATE_PASS_NO_TOKEN: "You don't have a token?",
    UPDATE_PASS_RESEND_EMAIL: "Resend email",
    UPDATE_PASS_SUCCESS: "Password changed successfully, redirecting to login page...",
    UPDATE_PASS_ERROR: "An error has occurred, try again later",

    WATCHLIST:'Watchlist',
    CRITIQUE:"Critique",
    DEBATE:"Debate",
    NEWS:"News",
    PAST_YEAR:'Last year',
    PAST_MONTH:'Last month',
    PAST_WEEK:'Last week',
    PAST_DAY:'Last 24hs',
    NEWEST:'Newest',
    OLDEST:'Oldest',
    HOTTEST:'Hottest',
    TITLE:'Title',
    MOST_POSTS: 'Most Posts',
    VOTES: 'Most votes',
    FOLLOWERS:'Most followers',
    USERNAME:'Username',
    ACTION:'Action',
    ADVENTURE:'Adventure',
    ANIMATION: 'Animation',
    COMEDY: 'Comedy',
    CRIME: 'Crime',
    DOCUMENTARY: 'Documentary',
    DRAMA: 'Drama',
    FAMILY: 'Family',
    FANTASY: 'Fantasy',
    HISTORY: 'History',
    HORROR: 'Horror',
    MUSIC: 'Music',
    MYSTERY: 'Mystery',
    ROMANCE: 'Romance',
    SCIENCE_FICTION: 'Science Fiction',
    TV_MOVIE: 'Tv Movie',
    THRILLER: 'Thriller',
    WAR: 'War',
    WESTERN: 'Western',
    USER: 'User',
    ADMIN: 'Admin',
    PAGINATION_PAGE_SIZE: "Showing",
    PROFILE_POST_TAB_DISPLAY: "Your posts",
    PROFILE_COMMENTS_TAB_DISPLAY: "Your comments",
    PROFILE_BOOK_TAB_DISPLAY: "Your bookmarked posts",
    PROFILE_FOLLOWED_USERS: "Your followed users",
    PROFILE_EDIT_DETAILS: "Edit information",
    PROFILE_SETTINGS_MODAL: "Settings",
	};
});
