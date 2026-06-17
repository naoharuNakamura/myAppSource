<script setup lang="ts">

import { ref, computed, onMounted } from "vue";
import { storeToRefs } from 'pinia';
import { useRouter } from "vue-router";
import { apiService } from "../services/api";
import { useAuthStore } from "../stores/auth";
import { ROUTE_NAMES, REGEX, HTTP_STATUS_CODES } from "../constants/types";
import { ERROR_MESSAGES, VALIDATION_MESSAGES, UI_TEXTS } from "../constants/messages";

const router = useRouter();
const authStore = useAuthStore();
const { currentUser } = storeToRefs(authStore);
const text = UI_TEXTS.signup;

// 1. ストアの値を直接 computed で監視することでリアクティブにする
const isNewSignUp = computed(() => !currentUser.value);


// 入力フォームの ref
const userPassword = ref("");
const reUserPassword = ref("");
const userName = ref("");
const userEmail = ref("");
const userPhone = ref("");

const handleSubmit = async () => {
    const isValid = await validateForm();
    if (!isValid) {
        return; // エラーがあればここで処理をストップ
    }

    try {
        // userId を一度定義し、条件によって含めるか決める
        const userData: any = {
            userPassword: userPassword.value,
            userName: userName.value,
            userEmail: userEmail.value,
            userPhone: userPhone.value
        };

        if (!isNewSignUp.value && currentUser.value) {
            userData.userId = currentUser.value.userId;
        }

        if (userPassword.value) {
            userData.userPassword = userPassword.value;
        }

        if (isNewSignUp.value) {
            await apiService.signup(userData);
            router.push({ name: ROUTE_NAMES.LOGIN });
        } else {
            const response = await apiService.updateProfile(userData);
            currentUser.value = response.data;
            router.push({ name: ROUTE_NAMES.MYPAGE });
        }
    } catch (error: any) {
        // 💡 確実にあらゆる場所からステータスコードを探し出し、数値(Number)に変換する
        const statusCode = error.response?.status || error.status || 0;

        if (Number(statusCode) === HTTP_STATUS_CODES.CONFLICT) {
            // 409（重複エラー）を検知！バナーは出さず、入力欄の下を赤文字にするだけ
            errors.value.userEmail = ERROR_MESSAGES.EMAIL_ALREADY_REGISTERED;
        } else {
            // 本当にサーバーが落ちている時（500）など、想定外の時だけバナーを出す
            console.error("サーバーエラー:", error);
            alert(ERROR_MESSAGES.SERVER_ERROR);
        }
    }
};

// 2. 編集モード（既存ユーザーがいる）なら初期値をセット
onMounted(() => {
    if (currentUser.value) {
        userName.value = currentUser.value.userName;
        userEmail.value = currentUser.value.userEmail;
        userPhone.value = currentUser.value.userPhone;
    }
});

const errors = ref({
    userPassword: "",
    reUserPassword: "",
    userName: "",
    userEmail: "",
    userPhone: ""
});

const validateForm = async () => {
    // 一度エラーメッセージをすべてクリア
    errors.value = {
        userPassword: "",
        reUserPassword: "",
        userName: "",
        userEmail: "",
        userPhone: ""
    };

    let isValid = true;

    if (isNewSignUp.value && !userPassword.value) {
        // 💡 新規登録で空の場合はエラー
        errors.value.userPassword = VALIDATION_MESSAGES.PASSWORD_REQUIRED;
        isValid = false;
    } else if (userPassword.value && !REGEX.PASSWORDREGEX.test(userPassword.value)) {
        // 💡 新規・更新問わず、「入力がある場合」のみ強度チェックを行う
        errors.value.userPassword = VALIDATION_MESSAGES.PASSWORD_WEAK;
        isValid = false;
    }

    // ③ パスワード再入力チェック
    // 💡 「パスワードに入力がある場合」のみ一致チェックを行う
    if (userPassword.value && userPassword.value !== reUserPassword.value) {
        errors.value.reUserPassword = VALIDATION_MESSAGES.PASSWORD_MISMATCH;
        isValid = false;
    }
    // ④ お名前（空チェック）
    if (!userName.value.trim()) {
        errors.value.userName = VALIDATION_MESSAGES.NAME_REQUIRED;
        isValid = false;
    }

    // ⑤ メールアドレス：形式チェック
    if (!userEmail.value) {
        errors.value.userEmail = VALIDATION_MESSAGES.EMAIL_REQUIRED;
        isValid = false;
    } else if (!REGEX.EMAILREGEX.test(userEmail.value)) {
        errors.value.userEmail = VALIDATION_MESSAGES.EMAIL_INVALID;
        isValid = false;
    } else {
        // --- ここから重複チェック ---
        try {
            if (!isNewSignUp.value && userEmail.value === currentUser?.value?.userEmail) {
            } else {
                // Spring BootのAPIを叩く
                const response = await apiService.getEmailExists(userEmail.value);

                if (response.data === true) {
                    errors.value.userEmail = ERROR_MESSAGES.EMAIL_ALREADY_REGISTERED;
                    isValid = false;
                }
            }
        } catch (error) {
            console.error(ERROR_MESSAGES.DUPLICATE_CHECK_FAILED, error);
            // 500エラーなどが起きた場合はとりあえずここに入る
            isValid = false;
        }
    }

    // ⑥ 電話番号：形式チェック（ハイフン有無両対応）
    if (!userPhone.value) {
        errors.value.userPhone = VALIDATION_MESSAGES.PHONE_REQUIRED;
        isValid = false;
    } else if (!REGEX.PHONEREGEX.test(userPhone.value)) {
        errors.value.userPhone = VALIDATION_MESSAGES.PHONE_INVALID;
        isValid = false;
    }

    return isValid;
};

</script>

<template>
    <div class="signup-container">
        <div class="signup-card">
            <p class="signup-subtitle">
                {{ isNewSignUp ? text.newTitle : text.editTitle }}
            </p>

            <form class="signup-form" @submit.prevent="handleSubmit">
                <div class="form-grid">
                    <div class="grid-column">
                        <div class="form-group">
                            <label for="userName">{{ text.nameLabel }}</label>
                            <input v-model="userName" id="userName" type="text" :placeholder="text.namePlaceholder"
                                class="form-control" :class="{ 'input-error': errors.userName }" required />
                            <span v-if="errors.userName" class="error-text">{{ errors.userName }}</span>
                        </div>

                        <div class="form-group">
                            <label for="userEmail">{{ text.emailLabel }}</label>
                            <input v-model="userEmail" id="userEmail" type="text" :placeholder="text.emailPlaceholder"
                                class="form-control" :class="{ 'input-error': errors.userEmail }" required />
                            <span v-if="errors.userEmail" class="error-text">{{ errors.userEmail }}</span>
                        </div>

                        <div class="form-group">
                            <label for="userPhone">{{ text.phoneLabel }}</label>
                            <input v-model="userPhone" id="userPhone" type="text" :placeholder="text.phonePlaceholder"
                                class="form-control" :class="{ 'input-error': errors.userPhone }" required />
                            <span v-if="errors.userPhone" class="error-text">{{ errors.userPhone }}</span>
                        </div>
                    </div>
                    <div class="grid-column">
                        <div class="form-group">
                            <label for="userPassword">{{ text.passwordLabel }} {{ isNewSignUp ? '' : text.passwordOptional }}</label>

                            <input v-model="userPassword" id="userPassword" type="password"
                                :placeholder="isNewSignUp ? text.passwordPlaceholderNew : text.passwordPlaceholderEdit" class="form-control"
                                :class="{ 'input-error': errors.userPassword }" :required="isNewSignUp" />

                            <span v-if="errors.userPassword" class="error-text">{{ errors.userPassword }}</span>
                        </div>

                        <div class="form-group">
                            <label for="reUserPassword">{{ text.rePasswordLabel }}</label>

                            <input v-model="reUserPassword" id="reUserPassword" type="password"
                                :placeholder="isNewSignUp ? text.rePasswordPlaceholderNew : text.rePasswordPlaceholderEdit"
                                class="form-control" :class="{ 'input-error': errors.reUserPassword }"
                                :disabled="!userPassword" :required="!!userPassword" />

                            <span v-if="errors.reUserPassword" class="error-text">{{ errors.reUserPassword }}</span>
                        </div>
                    </div>
                </div>
                <button type="submit" id="signup-btn" class="signup-btn">
                    {{ isNewSignUp ? text.submitNew : text.submitEdit }}
                </button>
            </form>
        </div>
    </div>
</template>

<style scoped>
.signup-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
    background-color: #fafafa;
    padding: 20px;
}

.signup-card {
    width: min(100%, 700px);
    /* 2カラムが綺麗に並ぶよう、PC時は最大幅を少し広めに確保 */
    max-width: 700px;
    padding: 32px;
    background-color: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    box-sizing: border-box;
}

.signup-subtitle {
    font-size: 16px;
    font-weight: bold;
    color: #4a5568;
    text-align: center;
    margin-bottom: 24px;
}

.signup-form {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* 💻 PC・タブレット環境（初期状態：横並び） */
.form-grid {
    display: flex;
    gap: 24px;
    width: 100%;
}

.grid-column {
    flex: 1;
    /* 左右のカラムを50%ずつの等幅にする */
    display: flex;
    flex-direction: column;
    gap: 18px;
    /* 縦方向の入力欄同士の間隔 */
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.form-group label {
    font-size: 13px;
    font-weight: 600;
    color: #4a5568;
    min-height: 18px;
    /* ラベルのない項目があっても高さがズレないように保護 */
}

.form-control {
    padding: 12px;
    font-size: 15px;
    border: 1px solid #cbd5e1;
    border-radius: 6px;
    outline: none;
    box-sizing: border-box;
    width: 100%;
    transition: border-color 0.2s, background-color 0.2s;
}

.form-control:focus {
    border-color: #3182ce;
}

/* エラー時の入力欄スタイル */
.form-control.input-error {
    border-color: #e53e3e;
    background-color: #fff5f5;
}

/* エラーメッセージのテキスト */
.error-text {
    color: #e53e3e;
    font-size: 12px;
    font-weight: 500;
    margin-top: 2px;
}

.signup-btn {
    margin-top: 10px;
    width: inherit;
    padding: 14px;
    font-size: 16px;
    font-weight: bold;
    color: #ffffff;
    background-color: #3182ce;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.signup-btn:hover {
    background-color: #2b6cb0;
}

/* 📱 スマホ環境（画面幅が440px以下になったら縦一列に変化） */
@media screen and (max-width: 440px) {
    .signup-card {
        padding: 20px;
    }

    .form-grid {
        flex-direction: column;
        /* 左右並びを縦並びに切り替え */
        gap: 18px;
    }

    .grid-column {
        gap: 18px;
    }
}
</style>