<script >
import {reactive} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

export default {
  name: "UserLogin",
  setup(){
    const data=reactive ({
      email:"",
      password: "",
      username:"",
    });
    const router =useRouter();
    const submit=async ()=>{
      const response=await axios.post('http://localhost:8080/api/auth/signin', data);
      axios.defaults.headers.common["Authorization"]=`Bearer ${response.data.jwt}`;

      await router.push("/menu");

    }
    return{
      data,
      submit
    }
  }
}

</script>

<template>

  <html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>CloudBOX | Responsive Bootstrap 4 Admin Dashboard Template</title>
  </head>
    <!-- Favicon -->

  <body class=" ">
  <!-- loader Start -->

  <!-- loader END -->

  <div class="wrapper">
    <section class="login-content">
      <div class="container h-100">
        <div class="row justify-content-center align-items-center height-self-center">
          <div class="col-md-5 col-sm-12 col-12 align-self-center">
            <div class="sign-user_card">
              <img src="../../../assets/images/logo.png" class="img-fluid rounded-normal light-logo logo" alt="logo">
              <img src="../../../assets/images/logo-white.png" class="img-fluid rounded-normal darkmode-logo logo" alt="logo">
              <h3 class="mb-3">Sign In</h3>
              <p>Login to stay connected.</p>
              <form @submit.prevent="submit" >
                <div class="row">
                  <div class="col-lg-12">
                    <div class="floating-label form-group">
                      <input v-model="data.email" class="floating-input form-control" type="email" placeholder=" ">
                      <label>Email</label>
                    </div>
                    <div class="floating-label form-group">
                      <input v-model='data.username'  class="floating-input form-control" type="text" placeholder=" ">
                      <label>Username</label>
                    </div>
                  </div>
                  <div class="col-lg-12">
                    <div class="floating-label form-group">
                      <input v-model='data.password' class="floating-input form-control" type="password" placeholder=" ">
                      <label>Password</label>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="custom-control custom-checkbox mb-3 text-left">
                      <input type="checkbox" class="custom-control-input" id="customCheck1">
                      <label class="custom-control-label" for="customCheck1">Remember Me</label>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <a href="" class="text-primary float-right">Forgot Password?</a>
                  </div>
                </div>
                <button type="submit" class="btn btn-primary">Sign In</button>
                <p class="mt-3">
                  Create an Account <a href="" class="text-primary">Sign Up</a>
                </p>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>

  <!-- Backend Bundle JavaScript -->
  </body>

  </html>
</template>

<style >
@import "../../../assets/css/backend-plugin.min.css ";
@import "../../../assets/css/backend.css?v=1.0.0 ";
@import "../../../assets/vendor/@fortawesome/fontawesome-free/css/all.min.css ";
@import "../../../assets/vendor/line-awesome/dist/line-awesome/css/line-awesome.min.css ";
@import "../../../assets/vendor/remixicon/fonts/remixicon.css ";
@import "../../../assets/vendor/doc-viewer/include/pdf/pdf.viewer.css ";
@import "../../../assets/vendor/doc-viewer/include/PPTXjs/css/pptxjs.css ";
@import "../../../assets/vendor/doc-viewer/include/PPTXjs/css/nv.d3.min.css ";
@import "../../../assets/vendor/doc-viewer/include/SheetJS/handsontable.full.min.css ";
@import "../../../assets/vendor/doc-viewer/include/verySimpleImageViewer/css/jquery.verySimpleImageViewer.css ";
@import "../../../assets/vendor/doc-viewer/include/officeToHtml/officeToHtml.css ";
</style>