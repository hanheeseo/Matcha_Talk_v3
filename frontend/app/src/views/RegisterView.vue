<template>
  <v-container class="py-10 bg-pink-lighten-5">
    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card class="pa-8">
          <div class="text-center text-h6 text-pink-darken-2 mb-6">회원가입</div>

          <v-form @submit.prevent="onSubmit">
            <v-text-field
              v-model="form.nick_name"
              label="이름"
              variant="outlined"
              class="mb-4"
              :error-messages="errors.nick_name"
              @blur="validate('nick_name')"
            />

            <div class="d-flex align-end mb-4">
              <v-text-field
                v-model="form.login_id"
                label="아이디"
                variant="outlined"
                class="flex-grow-1 me-2"
                :error-messages="errors.login_id"
                @blur="validate('login_id')"
              />
              <v-btn variant="outlined" color="pink" @click="checkLoginId" :disabled="loginIdAvailable">
                중복 확인
              </v-btn>
            </div>

            <div class="d-flex align-end mb-4">
              <v-text-field
                v-model="form.email"
                label="이메일"
                variant="outlined"
                class="flex-grow-1 me-2"
                :error-messages="errors.email"
                @blur="validate('email')"
                :disabled="emailVerified"
              />
              <v-btn
                variant="outlined"
                color="pink"
                @click="requestEmailVerify"
                :disabled="emailVerified"
              >이메일 인증</v-btn>
            </div>

            <div class="d-flex align-end mb-4" v-if="verificationSent && !emailVerified">
              <v-text-field
                v-model="verificationToken"
                label="인증번호"
                variant="outlined"
                class="flex-grow-1 me-2"
              />
              <v-btn variant="outlined" color="pink" @click="confirmEmailVerify">확인</v-btn>
            </div>
            <div class="text-caption text-green-darken-2 mb-4" v-if="emailVerified">
              이메일 인증 완료
            </div>

            <v-text-field
              v-model="form.password"
              type="password"
              label="비밀번호"
              variant="outlined"
              class="mb-4"
              :error-messages="errors.password"
              @blur="validate('password')"
            />

            <v-text-field
              v-model="form.password2"
              type="password"
              label="비밀번호 확인"
              variant="outlined"
              class="mb-4"
              :error-messages="errors.password2"
              @blur="validate('password2')"
            />

            <div class="mb-4">
              <div class="d-flex" style="gap: 8px;">
                <v-select
                  v-model="birth.year"
                  :items="yearItems"
                  label="년도"
                  variant="outlined"
                  class="flex-grow-1"
                  @blur="validate('birth')"
                />
                <v-select
                  v-model="birth.month"
                  :items="monthItems"
                  label="월"
                  variant="outlined"
                  class="flex-grow-1"
                  @blur="validate('birth')"
                />
                <v-select
                  v-model="birth.day"
                  :items="dayItems"
                  label="일"
                  variant="outlined"
                  class="flex-grow-1"
                  @blur="validate('birth')"
                />
              </div>
              <span class="text-caption text-pink-darken-2">{{ errors.birth }}</span>
            </div>

            <v-select
              v-model="form.gender"
              :items="genderItems"
              item-title="title"
              item-value="value"
              label="성별"
              variant="outlined"
              class="mb-4"
              :error-messages="errors.gender"
              @blur="validate('gender')"
            />

            <v-select
              v-model="form.language_code"
              :items="languageItems"
              item-title="title"
              item-value="value"
              label="사용 언어"
              variant="outlined"
              class="mb-4"
              :error-messages="errors.language_code"
              @blur="validate('language_code')"
            />

            <v-select
              v-model="form.country_code"
              :items="countryItems"
              label="국적"
              variant="outlined"
              class="mb-6"
              :error-messages="errors.country_code"
              @blur="validate('country_code')"
            />

            <v-btn type="submit" color="pink" block :disabled="!valid">회원가입</v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import api from '../services/api'
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// value는 반드시 'M' / 'F'
const genderItems = [
  { title: '남자', value: 'M' },
  { title: '여자', value: 'F' },
]
// 언어 코드는 백엔드 DTO가 소문자('ko','ja')를 요구합니다.
const languageItems = [
  { title: '한국어', value: 'ko' },
  { title: '일본어', value: 'ja' },
]
// 대문자 2글자 ISO 국가 코드
const countryItems = ['KR', 'JP']

const form = ref({
  nick_name: '',
  login_id: '',
  email: '',
  password: '',
  password2: '',
  gender: null as 'M' | 'F' | null,
  language_code: 'ko',
  country_code: null as string | null,
})

const birth = ref({ year: null as number | null, month: null as number | null, day: null as number | null })
const yearItems = Array.from({ length: 100 }, (_, i) => new Date().getFullYear() - i)
const monthItems = Array.from({ length: 12 }, (_, i) => i + 1)
const dayItems = Array.from({ length: 31 }, (_, i) => i + 1)

const errors = ref({
  nick_name: '',
  login_id: '',
  email: '',
  password: '',
  password2: '',
  birth: '',
  gender: '',
  language_code: '',
  country_code: '',
})

const verificationSent = ref(false)
const verificationToken = ref('')
const emailVerified = ref(false)
const loginIdAvailable = ref(false)

const valid = computed(() => {
  // emailVerified will be added later
  return Object.values(errors.value).every(e => !e) &&
    loginIdAvailable.value
})

const r = {
  required: (v: string | null) => !!v || '필수 입력입니다.',
  len: (min: number, max: number) => (v: string) => (v && v.length >= min && v.length <= max) || `${min}~${max}자`,
  email: (v: string) => /^[A-Za-z0-9._%+-]+@gmail\.com$/.test(v) || 'Gmail 주소만 사용 가능',
}

type Rule = (v: any) => string | boolean;

function checkRules(value: string, rules: Rule[]): string {
  for (const rule of rules) {
    const res = rule(value)
    if (res !== true) return res as string
  }
  return ''
}

function validate(field: string) {
  switch (field) {
    case 'nick_name':
      errors.value.nick_name = checkRules(form.value.nick_name, [r.required, r.len(2, 30)])
      break
    case 'login_id':
      errors.value.login_id = checkRules(form.value.login_id, [r.required, r.len(4, 30)])
      break
    case 'email':
      errors.value.email = checkRules(form.value.email, [r.required, r.email])
      break
    case 'password':
      errors.value.password = checkRules(form.value.password, [r.required, r.len(8, 100)])
      break
    case 'password2':
      errors.value.password2 = form.value.password2 === form.value.password ? '' : '비밀번호가 일치하지 않습니다.'
      break
    case 'birth': {
      const { year, month, day } = birth.value
      errors.value.birth = (year && month && day) ? '' : '생년월일을 입력하세요.'
      break
    }
    case 'gender':
      errors.value.gender = form.value.gender ? '' : '성별을 선택하세요.'
      break
    case 'language_code':
      errors.value.language_code = form.value.language_code ? '' : '사용 언어를 선택하세요.'
      break
    case 'country_code':
      errors.value.country_code = form.value.country_code ? '' : '국적을 선택하세요.'
      break
  }
}

watch(() => form.value.login_id, () => {
  loginIdAvailable.value = false
})

async function onSubmit() {
  ['nick_name', 'login_id', 'email', 'password', 'password2', 'birth', 'gender', 'language_code', 'country_code'].forEach(validate)

  // YYYY-MM-DD
  const birthDate = `${birth.value.year}-${String(birth.value.month).padStart(2, '0')}-${String(birth.value.day).padStart(2, '0')}`

  const payload = {
    loginId: form.value.login_id,
    password: form.value.password,
    nickName: form.value.nick_name,
    email: form.value.email,
    languageCode: form.value.language_code,
    countryCode: (form.value.country_code || '').toUpperCase(),
    gender: form.value.gender,
    birthDate
  }

  try {
    await api.post('/users/register', payload)
    alert('회원가입이 완료되었습니다.')
    router.push('/login')
  } catch (e: any) {
    alert('회원가입 실패: ' + (e?.response?.data?.message || e.message))
  }
}

async function requestEmailVerify() {
  validate('email')
  if (errors.value.email) return
  try {
    console.log('Requesting email verification for:', form.value.email)
    verificationSent.value = true
    alert('인증번호가 전송되었습니다. (구현 전)')
  } catch (e: any) {
    alert('인증번호 전송 실패: ' + (e?.response?.data?.message || e.message))
  }
}

async function checkLoginId() {
  validate('login_id')
  if (errors.value.login_id) return
  try {
    const { data } = await api.get('/users/exists', { params: { loginId: form.value.login_id } })
    if (data.exists) {
      errors.value.login_id = '이미 사용 중인 아이디입니다.'
      loginIdAvailable.value = false
      alert('이미 사용 중인 아이디입니다.')
    } else {
      errors.value.login_id = ''
      loginIdAvailable.value = true
      alert('사용 가능한 아이디입니다.')
    }
  } catch (e: any) {
    alert('아이디 확인 실패: ' + (e?.response?.data?.message || e.message))
  }
}

async function confirmEmailVerify() {
  try {
    console.log('Confirming email with token:', verificationToken.value)
    // Mocking success for now
    emailVerified.value = true
    alert('이메일 인증이 완료되었습니다. (구현 전)')
  } catch (e: any) {
    alert('인증번호 확인 실패: ' + (e?.response?.data?.message || e.message))
  }
}
</script>
