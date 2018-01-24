package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.BaseContract;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.TVRegistrationUpdateContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;

/**
 * Created by angelaanxa on 10/24/2017.
 */

public class SelectMealProfileActivity extends Activity {
    Button btnSave;
    CheckBox cbClassic;
    CheckBox cbMincir;
    CheckBox cbVegetarien;
    CheckBox cbColopathie;
    CheckBox cbBrunch;
    CheckBox cbNoBreakfast;
    CheckBox cbNoLunch;
    CheckBox cbNoDinner;
    CheckBox cbNoMilk;
    CheckBox cbNoDairy;
    CheckBox cbNoDairyNoCheese;
    CheckBox cbNoPorc;
    CheckBox cbNoGluten;

    LinearLayout progressLayout;

    ApiCaller caller;
    private LoginContract loginContract;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_selectmealprofile);
        if (ApplicationData.getInstance().regUserProfile == null) {
            goToLanding();
        }

        //header change
        ((TextView) (this.findViewById(R.id.header_title_tv))).setText(R.string.registration_myProfileHeader);
        ((TextView) (this.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.header_menu_iv)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.GONE);

        btnSave = (Button) findViewById(R.id.save_btn);
        cbClassic = (CheckBox) findViewById(R.id.cbClassic);
        cbMincir = (CheckBox) findViewById(R.id.cbMincir);
        cbVegetarien = (CheckBox) findViewById(R.id.cbVegetarien);
        cbColopathie = (CheckBox) findViewById(R.id.cbColopathie);
        cbBrunch = (CheckBox) findViewById(R.id.cbBrunch);
        cbNoBreakfast = (CheckBox) findViewById(R.id.cbNoBreakfast);
        cbNoLunch = (CheckBox) findViewById(R.id.cbNoLunch);
        cbNoDinner = (CheckBox) findViewById(R.id.cbNoDinner);
        cbNoMilk = (CheckBox) findViewById(R.id.cbNoMilk);
        cbNoDairy = (CheckBox) findViewById(R.id.cbNoDairy);
        cbNoDairyNoCheese = (CheckBox) findViewById(R.id.cbNoDairyNoCheese);
        cbNoPorc = (CheckBox) findViewById(R.id.cbNoPorc);
        cbNoGluten = (CheckBox) findViewById(R.id.cbNoGluten);

        progressLayout = (LinearLayout) findViewById(R.id.progress);
        progressLayout.setVisibility(View.GONE);

        caller = new ApiCaller();
    }

    public void validateForm(View view) {


        //disable submit button
        btnSave.setEnabled(false);


        if (validateForm()) {
            startProgress();

            //uncomment this after
//            goToRegistrationLastPage();
//            callOptinPage();

            updateRegistrationApi();

        } else {
            //form not validated
            btnSave.setEnabled(true);
        }
    }

    private boolean validateForm() {
        if (cbClassic.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(1);
            return true;
        }
        if (cbMincir.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(10);
            return true;
        }
        if (cbVegetarien.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(7);
            return true;
        }
        if (cbColopathie.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(5);
            return true;
        }
        if (cbBrunch.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(12);
            return true;
        }
        if (cbNoBreakfast.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(9);
            return true;
        }
        if (cbNoLunch.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(15);
            return true;
        }
        if (cbNoDinner.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(13);
            return true;
        }
        if (cbNoMilk.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(2);
            return true;
        }
        if (cbNoDairy.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(4);
            return true;
        }
        if (cbNoDairyNoCheese.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(4);
            return true;
        }
        if (cbNoPorc.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(6);
            return true;
        }
        if (cbNoGluten.isChecked()) {
            ApplicationData.getInstance().regUserProfile.setMealProfile(8);
            return true;
        }
        return false;
    }

    public void setCheckBoxValue(View view) {
        switch (view.getId()) {
            case R.id.cbClassic:

                if (cbClassic.isChecked()) {
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbMincir:

                if (cbMincir.isChecked()) {
                    cbClassic.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbVegetarien:

                if (cbVegetarien.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbColopathie:
                if (cbColopathie.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbBrunch:
                if (cbBrunch.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoBreakfast:

                if (cbNoBreakfast.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoLunch:
                if (cbNoLunch.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoDinner:

                if (cbNoDinner.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoMilk:

                if (cbNoMilk.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoDairy:

                if (cbNoDairy.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoDairyNoCheese:

                if (cbNoDairyNoCheese.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoPorc:

                if (cbNoPorc.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.cbNoGluten:

                if (cbNoGluten.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                }
                break;
            case R.id.imgClassic:
            case R.id.tv_Classic_title:

                if (cbClassic.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(true);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
                break;
            case R.id.tv_Mincir_title:
            case R.id.imgMincir: {
                if (cbMincir.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(true);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgVegetarien:
            case R.id.tv_Vegetarien_title: {
                if (cbVegetarien.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(true);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgColopathie:
            case R.id.tv_Colopathie_title: {
                if (cbColopathie.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(true);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgBrunch:
            case R.id.tv_Brunch_title: {
                if (cbBrunch.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(true);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoBreakfast:
            case R.id.tv_NoBreakfast_title: {
                if (cbNoBreakfast.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(true);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;

            case R.id.imgNoLunch:
            case R.id.tv_cbNoLunch_title: {
                if (cbNoLunch.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(true);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoDinner:
            case R.id.tv_NoDinner_title: {
                if (cbNoDinner.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(true);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoMilk:
            case R.id.tv_NoMilk_title: {
                if (cbNoMilk.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(true);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoDairy:
            case R.id.tv_NoDairy_title: {
                if (cbNoDairy.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(true);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoDairyNoCheese:
            case R.id.tv_NoDairyNoCheese_title: {
                if (cbNoDairyNoCheese.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(true);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoPorc:
            case R.id.tv_NoPorc_title: {
                if (cbNoPorc.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(true);
                    cbNoGluten.setChecked(false);
                }
            }
            break;
            case R.id.imgNoGluten:
            case R.id.tv_NoGluten_title: {
                if (cbNoGluten.isChecked()) {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(false);
                } else {
                    cbClassic.setChecked(false);
                    cbMincir.setChecked(false);
                    cbVegetarien.setChecked(false);
                    cbColopathie.setChecked(false);
                    cbBrunch.setChecked(false);
                    cbNoBreakfast.setChecked(false);
                    cbNoLunch.setChecked(false);
                    cbNoDinner.setChecked(false);
                    cbNoMilk.setChecked(false);
                    cbNoDairy.setChecked(false);
                    cbNoDairyNoCheese.setChecked(false);
                    cbNoPorc.setChecked(false);
                    cbNoGluten.setChecked(true);
                }
            }
            break;
            default:
        }
    }

    public void startProgress() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goToLanding() {
        Intent mainIntent = new Intent(this, LandingPageActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void goToRegistrationLastPage() {
        progressLayout.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        Intent mainIntent = new Intent(this, SelectRegistrationCuisinerActivity.class);
        startActivity(mainIntent);
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    private void updateRegistrationApi() {
        TVRegistrationUpdateContract contract = new TVRegistrationUpdateContract();
        contract.regId = ApplicationData.getInstance().regUserProfile.getRegId();
        contract.firstname = ApplicationData.getInstance().regUserProfile.getFirstname();
        contract.surname = ApplicationData.getInstance().regUserProfile.getLastname();
        contract.height = ApplicationData.getInstance().regUserProfile.getHeight();
        contract.weight = ApplicationData.getInstance().regUserProfile.getInitialWeight();
        contract.targetWeight = ApplicationData.getInstance().regUserProfile.getTargetWeight();
        if (ApplicationData.getInstance().regUserProfile.getGender().equalsIgnoreCase(getString(R.string.mon_compte_sexe_fem))) {
            contract.gender = false;
        } else {
            contract.gender = true;
        }
        contract.birthday = ApplicationData.getInstance().regUserProfile.getBday();
        contract.coachingProfile = ApplicationData.getInstance().regUserProfile.getCoaching();
        contract.mealProfile = ApplicationData.getInstance().regUserProfile.getMealProfile();
//        contract.cookingPrepTime = ApplicationData.getInstance().regUserProfile.getCuisiner();
        contract.cookingPrepTime = "0";
        contract.noCookingTrial = ApplicationData.getInstance().regUserProfile.getNoCookingTrial();
        caller.PostRegistrationUpdate(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {

                    BaseContract responseContract = (BaseContract) output;


                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                        autoLogin();
                    }
                }
            }
        }, contract, Integer.parseInt(ApplicationData.getInstance().regUserProfile.getRegId()));
    }

    private void autoLogin() {
        //login user in the background to obtain user details
        loginContract = new LoginContract();

        loginToAPI();
    }

    private void loginToAPI() {
        loginContract.Email = ApplicationData.getInstance().getSavedUserName();
        loginContract.Password = ApplicationData.getInstance().getSavedPassword();
        loginContract.Check_npna = true;

        caller.PostLogin(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

                if (output != null) {
                    UserDataResponseContract c = (UserDataResponseContract) output;

                    if (c != null) {
                        if (c.Message.equalsIgnoreCase("Failed")) {
                            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                        } else {
                            ApplicationData.getInstance().userDataContract = c.Data;
                            ApplicationData.getInstance().regId = c.Data.Id;

                            ApplicationData.getInstance().setIsLogin(getBaseContext(), true);
                            ApplicationData.getInstance().saveLoginCredentials(loginContract.Email, loginContract.Password);
                            //updateGoogleOrder();
                            goToAccountPage();
                        }
                    } else {
                        displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                    }
                }

            }
        }, loginContract);

    }

    public void displayToastMessage(final String message) {
        final Context context = this;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast m = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                m.show();

            }
        });
    }

    private void goToAccountPage() {
        if (ApplicationData.getInstance().isLoggedIn(getBaseContext())) {
            ApplicationData.getInstance().accountType = "account";
            Intent mainIntent = new Intent(this, LandingPageAccountActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

}
