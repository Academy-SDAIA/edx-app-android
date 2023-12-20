package org.edx.mobile.view.login

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import org.edx.mobile.BuildConfig
import org.edx.mobile.R
import org.edx.mobile.authentication.LoginAPI
import org.edx.mobile.base.BaseFragmentActivity
import org.edx.mobile.databinding.ActivityLoginBinding
import org.edx.mobile.deeplink.DeepLink
import org.edx.mobile.deeplink.DeepLinkManager
import org.edx.mobile.exception.LoginErrorMessage
import org.edx.mobile.exception.LoginException
import org.edx.mobile.extenstion.addAfterTextChanged
import org.edx.mobile.extenstion.isNotNullOrEmpty
import org.edx.mobile.extenstion.parcelable
import org.edx.mobile.extenstion.setVisibility
import org.edx.mobile.http.HttpStatus
import org.edx.mobile.http.HttpStatusException
import org.edx.mobile.model.nafath.NafathRegisterUserCheckStatusRequest
import org.edx.mobile.model.nafath.NafathRegisterUserRequest
import org.edx.mobile.module.analytics.Analytics
import org.edx.mobile.module.prefs.LoginPrefs
import org.edx.mobile.social.SocialAuthSource
import org.edx.mobile.social.SocialLoginDelegate
import org.edx.mobile.social.SocialLoginDelegate.Feature
import org.edx.mobile.social.SocialLoginDelegate.MobileLoginCallback
import org.edx.mobile.util.AppConstants
import org.edx.mobile.util.AppStoreUtils
import org.edx.mobile.util.AuthUtils
import org.edx.mobile.util.IntentFactory
import org.edx.mobile.util.NetworkUtil
import org.edx.mobile.util.TextUtils
import org.edx.mobile.util.UtilityFunction.educationList
import org.edx.mobile.util.UtilityFunction.employmentList
import org.edx.mobile.util.UtilityFunction.englishLevelList
import org.edx.mobile.util.UtilityFunction.getEducationKey
import org.edx.mobile.util.UtilityFunction.getEmployeeKey
import org.edx.mobile.util.UtilityFunction.getExperienceKey
import org.edx.mobile.util.UtilityFunction.getReginKey
import org.edx.mobile.util.UtilityFunction.reginList
import org.edx.mobile.util.UtilityFunction.workExperienceList
import org.edx.mobile.util.images.ErrorUtils
import org.edx.mobile.util.observer.EventObserver
import org.edx.mobile.util.widget.CustomAlertDialog
import org.edx.mobile.util.widget.DialogTypes
import org.edx.mobile.view.Router
import org.edx.mobile.view.dialog.ResetPasswordDialogFragment
import org.edx.mobile.viewModel.AuthViewModel
import java.util.Calendar


@AndroidEntryPoint
class LoginActivity : BaseFragmentActivity(), MobileLoginCallback {

    private lateinit var socialLoginDelegate: SocialLoginDelegate
    private lateinit var binding: ActivityLoginBinding
    private lateinit var alertDialog: CustomAlertDialog

    private val authViewModel: AuthViewModel by viewModels()
    private var gender :String = "m"
    private var  regin:String = ""
    private var  education:String = ""
    private var  englishLevel:String = ""
    private var  employment:String = ""
    private var  experience:String = ""
    private var  dataOfBirth:String = ""
    private var  dataOfBirthRequestFormat:String = ""
    private var  dataOfBirthYear:Int = 0
    private var  transId:String = ""
    val email: String
        get() = binding.emailEt.text.toString().trim()

    val password: String
        get() = binding.passwordEt.text.toString().trim()

    private val loginException: LoginException
        get() = LoginException(
            LoginErrorMessage(
                getString(R.string.login_error),
                getString(R.string.login_failed)
            )
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog()
        initViews()
        initObservers()
        hideSoftKeypad()
        // enable login buttons at launch
        tryToSetUIInteraction(true)
        environment.analyticsRegistry.trackScreenView(Analytics.Screens.LOGIN)
    }

    private fun initViews() {
        setToolbarAsActionBar()
        title = getString(R.string.login_title)
        binding.loginButtonLayout.setOnClickListener {
            callServerForLogin()
        }
        binding.forgotPasswordTv.setOnClickListener {
            if (NetworkUtil.isConnected(this@LoginActivity)) {
                showResetPasswordDialog()
            } else {
                showAlertDialog(
                    getString(R.string.reset_no_network_title),
                    getString(R.string.network_not_connected)
                )
            }
        }

        // TODO: Nafath
        binding.forgotPasswordTv.visibility = View.GONE
        binding.nafathPinWrapper.visibility = View.GONE

        binding.loginButtonLayout.visibility=View.GONE
        binding.nafathButtonLayout.visibility=View.VISIBLE
        binding.llNafathRegistration.visibility=View.GONE

        binding.nafathUserNameEt.inputType = InputType.TYPE_CLASS_TEXT
        binding.nafathUserNameEt.filters = arrayOf<InputFilter>(
            InputFilter { src, start, end, dst, dstart, dend ->
                if (src == "") { return@InputFilter src }
                if (src.toString().matches("[a-zA-Z0-9_-]+".toRegex())) { src } else ""
            }
        )
        binding.btnNafath.setOnClickListener {
            binding.llEdx.visibility = View.GONE
            binding.llNafath.visibility = View.VISIBLE
            binding.forgotPasswordTv.visibility = View.GONE
            binding.nafathPinWrapper.visibility = View.GONE
            binding.loginBtnTv.text = "Authenticate with Nafath App"

            binding.btnNafath.background = resources.getDrawable(R.drawable.nafath_btn_selector)
            //  binding.btnEdx.background = resources.getDrawable(R.drawable.nafath_btn_un_selector)

//            try {
//                binding.btnEdx.setTextColor(resources.getColor(R.color.black))
//                binding.btnNafath.setTextColor(resources.getColor(R.color.secondaryBaseColor))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            binding.loginButtonLayout.visibility=View.GONE
            binding.nafathButtonLayout.visibility=View.VISIBLE
        }

//        binding.btnEdx.setOnClickListener {
//            binding.llNafath.visibility = View.GONE
//            binding.llEdx.visibility = View.VISIBLE
//            binding.loginBtnTv.text = resources.getString(R.string.signing_in)
//            binding.forgotPasswordTv.visibility = View.VISIBLE
//
//            binding.btnNafath.background = resources.getDrawable(R.drawable.nafath_btn_un_selector)
//            binding.btnEdx.background = resources.getDrawable(R.drawable.nafath_btn_selector)
//            try {
//                binding.btnEdx.setTextColor(resources.getColor(R.color.secondaryBaseColor))
//                binding.btnNafath.setTextColor(resources.getColor(R.color.black))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            binding.loginButtonLayout.visibility=View.VISIBLE
//            binding.nafathButtonLayout.visibility=View.GONE
//            binding.llNafathRegistration.visibility=View.GONE
//
//        }




        binding.nafathButtonRegisterLayout.setOnClickListener {

            if (binding.nafathNameEt.text.isNullOrEmpty()){
                Toast.makeText(this,"Please Enter Full name ",Toast.LENGTH_SHORT).show()
            }else if(binding.nafathUserNameEt.text.isNullOrEmpty() || binding.nafathUserNameEt.text!!.length<3 ){
                Toast.makeText(this,"username must be between 3 and 30 character",Toast.LENGTH_SHORT).show()
            }else if (checkValidation(binding.nafathEmailEt.text.toString().trim())){
                Toast.makeText(this,"Enter your valid email",Toast.LENGTH_SHORT).show()
            }else if (binding.nafathPhoneEt.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter your valid phone number",Toast.LENGTH_SHORT).show()
            }else if (regin.isEmpty()){
                Toast.makeText(this,"Select your region",Toast.LENGTH_SHORT).show()
            }else if (binding.nafathCityEt.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter your city where you live",Toast.LENGTH_SHORT).show()
            }else if (education.isEmpty()){
                Toast.makeText(this,"Select your level of education",Toast.LENGTH_SHORT).show()
            }else if(employment.isEmpty()){
                Toast.makeText(this,"Select your employment status",Toast.LENGTH_SHORT).show()
            }else if (experience.isEmpty()){
                Toast.makeText(this,"Select your work experience level",Toast.LENGTH_SHORT).show()
            }else if (binding.nafathJobTitleEt.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter your current job title",Toast.LENGTH_SHORT).show()
            }else if (gender.isEmpty()){

            }else if (dataOfBirth.isEmpty()){
                Toast.makeText(this,"Enter your Date of birth",Toast.LENGTH_SHORT).show()
            }else{
                if (binding.nafathCheckbox.isChecked){
                    authViewModel.registerUser(NafathRegisterUserRequest(binding.nafathEt.text.toString(),transId,"",NafathRegisterUserRequest.User(binding.nafathUserNameEt.text.toString(),binding.nafathEmailEt.text.toString(),"",6)))
                }
              //  val maps: HashMap<Any, Any> = hashMapOf("name" to binding.nafathNameEt.text.toString(),"username" to binding.nafathUserNameEt.text.toString(), "email" to binding.nafathEmailEt.text.toString(),"phone_number" to binding.nafathPhoneEt.text.toString(),"gender" to "m","date_of_birth" to dataOfBirth.toString(), "region" to regin.toString(), "city" to binding.nafathCityEt.text.toString(),"address_line" to binding.nafathAddressLineEt.text.toString(), "level_of_education" to education.toString(),"english_language_level" to englishLevel.toString(),"employment_status" to employment.toString(),"work_experience_level" to experience.toString(), "job_title" to binding.nafathJobTitleEt.text.toString(),"activation_code" to "00")
            }
        }

        // TODO: Final call for registration
        binding.nafathButtonRegisterFinalLayout.setOnClickListener {
            if (binding.nafathActivationCodeEt.text!!.isNotEmpty()){
                authViewModel.registerUserCheckStatus(NafathRegisterUserCheckStatusRequest(binding.nafathEt.text.toString(),transId,"true",NafathRegisterUserCheckStatusRequest.User(binding.nafathNameEt.text.toString(),binding.nafathUserNameEt.text.toString(),binding.nafathEmailEt.text.toString(),binding.nafathPhoneEt.text.toString(),"m","",dataOfBirthRequestFormat,regin,binding.nafathCityEt.text.toString(),binding.nafathAddressLineEt.text.toString(),education,englishLevel,employment,experience,binding.nafathJobTitleEt.text.toString(),binding.nafathActivationCodeEt.text.toString(),dataOfBirthYear)))
            }else{
                Toast.makeText(this,"Please Enter Valid activation code",Toast.LENGTH_SHORT).show()
            }
        }

        binding.llCalendar.setOnClickListener {
            // Get Current Date
            val cal: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this,
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate: String =  (monthOfYear +1 ).toString() + "/" + dayOfMonth.toString()  + "/" + year
                    binding.tvCalendar.text =selectedDate
                    dataOfBirth= selectedDate
                    dataOfBirthRequestFormat= year.toString() +  "-" + (monthOfYear +1 ) .toString()+ "-" +dayOfMonth.toString()
                    dataOfBirthYear=  year
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = cal.timeInMillis
            datePickerDialog.show()
        }

        binding.nafathButtonLayout.setOnClickListener {
            if (binding.nafathEt.text!!.isNotEmpty()){
                authViewModel.InitiateRequest(binding.nafathEt.text.toString())
                alertDialog.show()
            }
        }


        binding.emailEt.addAfterTextChanged {
            binding.usernameWrapper.error = null
        }
        binding.passwordEt.addAfterTextChanged {
            binding.passwordWrapper.error = null
        }
        setupSocialLogin()
        initEULA()
    }


    private fun registerForm(){
        binding.llNafath.visibility=View.GONE
        binding.llNafathRegistration.visibility=View.VISIBLE

        val adapter = ArrayAdapter<String>(this,R.layout.spinner_item,R.id.tv_spinner,reginList)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        binding.nafathRegionSpinner.adapter= adapter

        binding.nafathRegionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                regin= getReginKey(reginList[position]) }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // TODO: education

        val adapterEducation = ArrayAdapter<String>(this,R.layout.spinner_item,R.id.tv_spinner,educationList)
        adapterEducation.setDropDownViewResource(R.layout.spinner_item)
        binding.nafathEducationSpinner.adapter= adapterEducation

        binding.nafathEducationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                education= getEducationKey(educationList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // TODO: English

        val adapterEnglish = ArrayAdapter<String>(this,R.layout.spinner_item,R.id.tv_spinner,englishLevelList)
        adapterEnglish.setDropDownViewResource(R.layout.spinner_item)
        binding.nafathEnglishLevelSpinner.adapter= adapterEnglish

        binding.nafathEnglishLevelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                englishLevel= englishLevelList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // TODO: Employment status:

        val adapterEmployment = ArrayAdapter<String>(this,R.layout.spinner_item,R.id.tv_spinner,employmentList)
        adapterEmployment.setDropDownViewResource(R.layout.spinner_item)
        binding.nafathEmploymentStatusSpinner.adapter= adapterEmployment

        binding.nafathEmploymentStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                employment= getEmployeeKey(employmentList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // TODO: Work Experience

        val adapterWorkExperience = ArrayAdapter<String>(this,R.layout.spinner_item,R.id.tv_spinner,workExperienceList)
        adapterWorkExperience.setDropDownViewResource(R.layout.spinner_item)

        binding.nafathExperienceSpinner.adapter= adapterWorkExperience

        binding.nafathExperienceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                experience= getExperienceKey( workExperienceList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
    }
    private fun checkValidation(email :String):Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setupSocialLogin() {
        val googleEnabled = environment.config.googleConfig.isEnabled
        val facebookEnabled = environment.config.facebookConfig.isEnabled
        val microsoftEnabled = environment.config.microsoftConfig.isEnabled

        binding.socialAuth.apply {
            root.setVisibility((!facebookEnabled && !googleEnabled && !microsoftEnabled).not())
            googleButton.setVisibility(googleEnabled)
            facebookButton.setVisibility(facebookEnabled)
            microsoftButton.setVisibility(microsoftEnabled)
        }

        socialLoginDelegate = SocialLoginDelegate(
            this, this,
            environment.config, environment.loginPrefs, Feature.SIGN_IN
        ).apply {
            binding.socialAuth.facebookButton.setOnClickListener(
                createSocialButtonClickHandler(SocialAuthSource.FACEBOOK)
            )
            binding.socialAuth.googleButton.setOnClickListener(
                createSocialButtonClickHandler(SocialAuthSource.GOOGLE)
            )
            binding.socialAuth.microsoftButton.setOnClickListener(
                createSocialButtonClickHandler(SocialAuthSource.MICROSOFT)
            )
        }
    }

    private fun initObservers() {
        authViewModel.onLogin.observe(this, EventObserver {
            if (it) {
                onUserLoginSuccess()
            }
        })

        authViewModel.showProgress.observe(this, EventObserver {
            binding.progress.progressIndicator.setVisibility(it)
        })

        authViewModel.errorMessage.observe(this, EventObserver {
            val ex = it.throwable
            if (ex is HttpStatusException && ex.statusCode == HttpStatus.BAD_REQUEST) {
                onUserLoginFailure(loginException)
            } else {
                onUserLoginFailure(ex as Exception)
            }
        })

        authViewModel.socialLoginErrorMessage.observe(this, EventObserver {
            if (it.throwable is LoginAPI.AccountNotLinkedException) {
                onUserLoginFailure(
                    LoginException(
                        AuthUtils.getLoginErrorMessage(
                            activity = this,
                            config = environment.config,
                            backend = environment.loginPrefs.socialLoginProvider,
                            feature = Feature.SIGN_IN,
                            e = it.throwable
                        )
                    )
                )
            } else {
                onUserLoginFailure(it.throwable as Exception)
            }
        })

        authViewModel.initiateRequest.observe(this, Observer { it
            if (it.peekContent().code=="400"){
                alertDialog.dismiss()
                Toast.makeText(this,it.peekContent().message,Toast.LENGTH_SHORT).show()
            }else{
                it.peekContent().random
                binding.nafathPinWrapper.visibility=View.VISIBLE
                binding.nafathEt.isEnabled=false
                binding.nafathPinEt.setText(it.peekContent().random)
                binding.nafathPinEt.isEnabled=false
                transId= it.peekContent().transId
                val checstatusRequest: HashMap<String, String> = hashMapOf("nafath_id" to binding.nafathEt.text.toString(),"trans_id" to transId, "is_mobile_app" to "true","client_id" to AppConstants.CLIENT_ID)
                authViewModel.checkStatus(checstatusRequest)
            }


        } )

        authViewModel.checkStatus.observe(this, Observer { it
            if (it.peekContent().code=="400"){
                Toast.makeText(this,it.peekContent().message,Toast.LENGTH_SHORT).show()
            }else{
                when(it.peekContent().status){
                    "WAITING" ->{
                        Handler().postDelayed(Runnable {
                            val checstatusRequest: HashMap<String, String> = hashMapOf("nafath_id" to binding.nafathEt.text.toString(),"trans_id" to transId, "is_mobile_app" to "true","client_id" to AppConstants.CLIENT_ID)
                            authViewModel.checkStatus(checstatusRequest)
                        }, 5000)
                    }
                    "REGISTERED" ->{
                        alertDialog.dismiss()
                        val checkStatusRequest: HashMap<String, String> = hashMapOf("trans_id" to transId,"is_mobile_app" to "true","client_id" to AppConstants.CLIENT_ID, "nafath_id" to binding.nafathEt.text.toString())
                        authViewModel.loginUsingNafath(checkStatusRequest)
                    }
                    "COMPLETED" ->{
                        alertDialog.dismiss()
                        registerForm()
                    }
                    "EXPIRED" ->{
                        alertDialog.dismiss()
                        binding.nafathPinWrapper.visibility=View.GONE
                        binding.nafathEt.isEnabled=true
                        binding.nafathPinEt.setText("")
                        binding.nafathPinEt.isEnabled=true
                    }
                    else ->{
                        alertDialog.dismiss()
                        binding.nafathPinWrapper.visibility=View.GONE
                        binding.nafathEt.isEnabled=true
                        binding.nafathPinEt.setText("")
                        binding.nafathPinEt.isEnabled=true
                    }

                }
            }



        } )

        authViewModel.initCheckStatusRegisterUser.observe(this, Observer { it

            if (it.peekContent().code=="400"){
                Toast.makeText(this,it.peekContent().message,Toast.LENGTH_SHORT).show()
            }else{
                when(it.peekContent().status){
                    "REGISTERED" ->{
                        val checkStatusRequest: HashMap<String, String> = hashMapOf("trans_id" to transId,"is_mobile_app" to "true","client_id" to AppConstants.CLIENT_ID, "nafath_id" to binding.nafathEt.text.toString())
                        authViewModel.loginUsingNafath(checkStatusRequest)
                    }
                    else ->{}
                }
            }


        } )



        authViewModel.initRegisterUser.observe(this, Observer { it
            if (it.peekContent().code =="400"){
                Toast.makeText(this,it.peekContent().successMessage,Toast.LENGTH_SHORT).show()
            }else{
                binding.llNafathRegistration.visibility=View.GONE
                binding.llNafathRegistrationFinal.visibility=View.VISIBLE
            }
        } )
    }

    override fun performUserLogin(accessToken: String, backend: String, feature: Feature) {
        tryToSetUIInteraction(false)
        authViewModel.loginUsingSocialAccount(accessToken, backend, feature)
    }

    private fun initEULA() {
        binding.endUserAgreementTv.movementMethod = LinkMovementMethod.getInstance()
        binding.endUserAgreementTv.text = TextUtils.generateLicenseText(
            environment.config, this, R.string.by_signing_in
        )

        val envDisplayName = environment.config.environmentDisplayName
        if (envDisplayName.isNotNullOrEmpty()) {
            binding.versionEnvTv.setVisibility(true)
            val versionName = BuildConfig.VERSION_NAME
            val text = String.format(
                "%s %s %s",
                getString(R.string.label_version),
                versionName,
                envDisplayName
            )
            binding.versionEnvTv.text = text
        }
    }

    override fun configureActionBar() {
        super.configureActionBar()
        if (environment.config.isRegistrationEnabled.not()) {
            supportActionBar?.apply {
                setHomeButtonEnabled(false)
                setDisplayHomeAsUpEnabled(false)
                setDisplayShowHomeEnabled(false)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("username", email)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    override fun onStart() {
        super.onStart()
        if (email.isEmpty()) {
            displayLastEmailId()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.emailEt.setText(savedInstanceState.getString("username"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tryToSetUIInteraction(true)
        socialLoginDelegate.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ResetPasswordDialogFragment.REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    showAlertDialog(
                        getString(R.string.success_dialog_title_help),
                        getString(R.string.success_dialog_message_help)
                    )
                }
            }
        }
    }

    private fun displayLastEmailId() {
        binding.emailEt.setText(environment.loginPrefs.lastAuthenticatedEmail)
    }

    private fun callServerForLogin() {
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(
                getString(R.string.no_connectivity),
                getString(R.string.network_not_connected)
            )
            return
        }
        if (password.isEmpty()) {
            binding.passwordWrapper.error = getString(R.string.error_enter_password)
            binding.passwordEt.requestFocus()
        }
        if (email.isEmpty()) {
            binding.usernameWrapper.error = getString(R.string.error_enter_email)
            binding.emailEt.requestFocus()
        }
        if (email.isNotEmpty() && password.isNotEmpty()) {
            binding.emailEt.isEnabled = false
            binding.passwordEt.isEnabled = false
            binding.forgotPasswordTv.isEnabled = false
            binding.endUserAgreementTv.isEnabled = false

            authViewModel.loginUsingEmail(email, password)
            tryToSetUIInteraction(false)
        }
    }

    private fun showResetPasswordDialog() {
        ResetPasswordDialogFragment.newInstance(email).show(supportFragmentManager, null)
    }

    // make sure that on the login activity, all errors show up as a dialog as opposed to a flying snackbar
    override fun showAlertDialog(header: String?, message: String) {
        super.showAlertDialog(header, message)
    }

    private fun onUserLoginSuccess() {
        setResult(RESULT_OK)
        finish()
        val deepLink = intent.parcelable<DeepLink>(Router.EXTRA_DEEP_LINK)
        if (deepLink != null) {
            DeepLinkManager.onDeepLinkReceived(this, deepLink)
            return
        }
        if (!environment.config.isRegistrationEnabled) {
            environment.router.showMainDashboard(this)
        }
    }

    private fun onUserLoginFailure(ex: Exception) {
        tryToSetUIInteraction(true)
        when (ex) {
            is LoginException -> {
                val errorMessage = ex.loginErrorMessage
                showAlertDialog(errorMessage.messageLine1, errorMessage.messageLine2)
            }

            is HttpStatusException -> {
                when (ex.statusCode) {
                    HttpStatus.UPGRADE_REQUIRED -> this@LoginActivity.showAlertDialog(
                        null,
                        getString(R.string.app_version_unsupported_login_msg),
                        getString(R.string.label_update),
                        { _, _ ->
                            AppStoreUtils
                                .openAppInAppStore(this@LoginActivity)
                        },
                        getString(android.R.string.cancel), null
                    )

                    HttpStatus.FORBIDDEN -> this@LoginActivity.showAlertDialog(
                        getString(R.string.login_error),
                        getString(R.string.auth_provider_disabled_user_error),
                        getString(R.string.label_customer_support),
                        { _, _ ->
                            environment.router
                                .showFeedbackScreen(
                                    this@LoginActivity,
                                    getString(R.string.email_subject_account_disabled)
                                )
                        }, getString(android.R.string.cancel), null
                    )

                    else -> {
                        showAlertDialog(
                            getString(R.string.login_error),
                            ErrorUtils.getErrorMessage(ex, this@LoginActivity)
                        )
                        logger.error(ex)
                    }
                }
            }

            else -> {
                showAlertDialog(
                    getString(R.string.login_error),
                    ErrorUtils.getErrorMessage(ex, this@LoginActivity)
                )
                logger.error(ex)
            }
        }
    }

    override fun tryToSetUIInteraction(enable: Boolean): Boolean {
        setTouchEnabled(enable)
        binding.apply {
            loginButtonLayout.isEnabled = enable
            emailEt.isEnabled = enable
            passwordEt.isEnabled = enable
            loginBtnTv.text = getString(if (enable) R.string.login_title else R.string.signing_in)
            forgotPasswordTv.isEnabled = enable
            socialAuth.facebookButton.isClickable = enable
            socialAuth.googleButton.isClickable = enable
            socialAuth.microsoftButton.isClickable = enable
            endUserAgreementTv.isEnabled = enable
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newIntent(deepLink: DeepLink?): Intent {
            val intent = IntentFactory.newIntentForComponent(LoginActivity::class.java)
            intent.putExtra(Router.EXTRA_DEEP_LINK, deepLink)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            return intent
        }
    }

    private fun progressDialog() {
        alertDialog =
            CustomAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading")
                .setDescription("Please Wait").build()
        alertDialog.setCancelable(false)
    }
}
