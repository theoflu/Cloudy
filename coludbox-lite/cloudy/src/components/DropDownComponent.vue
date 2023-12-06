<script >
import { FwbDropdown, FwbListGroup, FwbListGroupItem,} from 'flowbite-vue'
import {defineComponent} from "vue";
import axios from "axios";

export default defineComponent({
  components: {FwbListGroupItem, FwbListGroup, FwbDropdown},
  methods:{
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

  }
})


</script>

<template>
  <fwb-dropdown text="Bottom">
    <template #trigger>
      <span class="dropdown-toggle" id="dropdownMenuButton6" data-toggle="dropdown" aria-expanded="false">
        <i class="ri-more-fill"></i>
      </span>
    </template>
    <fwb-list-group class="fwb-list-group">
      <fwb-list-group-item  class="fwb-list-group-item">
          Profile
      </fwb-list-group-item>
      <fwb-list-group-item  class="fwb-list-group-item" @click="getFiles(item.filename)">
        Download
      </fwb-list-group-item>
      <fwb-list-group-item  class="fwb-list-group-item">
        Messages
      </fwb-list-group-item>
      <fwb-list-group-item  class="fwb-list-group-item">
        Download
      </fwb-list-group-item>
    </fwb-list-group>
  </fwb-dropdown>
</template>



<style>
.dropdown-toggle {
  /* Dropdown tetikleyici öğesi için özel stiller */

}

.fwb-list-group {
  /* Öğe listesi bileşeni için özel stiller */
  color: gold;
  cursor: grab;

}


.fwb-list-group-item {
  display: block;
  width: 100%;
  padding: 0.25rem 1.5rem;
  clear: both;
  font-weight: 400;
  color: #212529;
  text-align: inherit;
  white-space: nowrap;
  background-color: transparent;
  border: 0; }
.fwb-list-group-item:hover, .fwb-list-group-item:focus {
  color: #16181b;
  text-decoration: none;
  background-color: #f8f9fa; }
.fwb-list-group-item.active, .fwb-list-group-item:active {
  color: #fff;
  text-decoration: none;
  background-color: #8f93f6; }
.fwb-list-group-item.disabled, .fwb-list-group-item:disabled {
  color: #6c757d;
  pointer-events: none;
  background-color: transparent; }

</style>