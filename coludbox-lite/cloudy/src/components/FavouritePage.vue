<script >

import {defineComponent} from "vue";
import HelloWorld from "@/components/HelloWorld.vue";
import {getAll} from "@/common/user-service";
import axios from "axios";
import DropDownComponent from "@/components/DropDownComponent.vue";



export default defineComponent({
  components: {DropDownComponent, HelloWorld},
  name: 'FavouritePage',
  el: '.checkbox',
  props: {
    msg: String
  },
  data() {
    return {
      files: [],
      jwt: "",
      durum: [],
      datsa: {
        filename:"",
        isCheck:false,

      }
      ,showDropdown:false


    }
  },
  created() {
    this.jwt = localStorage.getItem('accessToken');
    const customHeaders = {
      'Authorization': "Bearer " + this.jwt,

    };

    this.getfilelists(customHeaders);



  }, methods: {
    toggleDropdown() {
      this.showDropdown = !this.showDropdown;
      console.log(this.showDropdown);
    },

    getfilelists(customHeaders) {

      getAll(customHeaders).then(response => {
        this.files = response.data;
        console.log( this.files);
      })
    },
    async getFiles(filename) {
      try {
        const response = await axios.get(`user/download/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,

          },
          responseType: 'blob' // Dosyayı binary olarak almak için blob kullanılır
        });
        console.log(response);
        const url = window.URL.createObjectURL(new Blob([response.data]));

        // Dosyayı indirmek için bir <a> etiketi oluşturma ve tıklama işlemi
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        link.setAttribute('target', '_blank');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        return response.data; // Dosya içeriğini istemciye döndür
      } catch (error) {
        throw new Error('Dosya alınamadı:', error);
      }
    },
    async checkCheckbox(item) {

      this.datsa.filename=item.filename;
      this.datsa.isCheck=item.favourite;
      await axios.post(`user/favourite`, this.datsa, {
        headers: {
          'Authorization': "Bearer " + this.jwt,

        },
      });


    }
  }
})
</script>

<template>
  <HelloWorld/>
  <div class="content-page">
    <div class="container-fluid">
      <div class="icon icon-grid i-grid">
        <div class="row">
          <div class="col-lg-12">
            <div class="card card-block card-stretch card-height">
              <div class="card-body">
                <div class="table-responsive">
                  <table class="table mb-0 table-borderless tbl-server-info">
                    <thead>
                    <tr>
                      <th scope="col">Name</th>
                      <th scope="col">Owner</th>
                      <th scope="col">Last Edit</th>
                      <th scope="col">File Size</th>
                      <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for='(item, index) in files' :key='index'>
                      <td>
                        <div class="d-flex align-items-center">
                          <div class="mr-3">
                            <a href="#"><img src="../../../assets/images/layouts/page-6/01.png" class="img-fluid avatar-30" alt="image1"></a>
                          </div>
                         {{item.filename}}
                        </div>
                      </td >
                      <td>Me</td>
                      <td>jan 21, 2020 me</td>
                      <td>{{item.fileDetail.filesize/1000000000}} GB</td>
                      <td>
                        <DropDownComponent/>

                      </td>
                    </tr>

                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>

</template>

<style >


</style>