<template>
  <div>
    <input
        class="dropdown-toggle search-query text search-input"
        type="text"
        placeholder="Type here to search..."
        v-model="searchTerm"
    />
    <span class="search-replace"></span>

    <!-- Filtrelenmiş öğeleri listeleyen bir bölüm -->
    <ul>
      <li v-for="item in filteredItems" :key="item.id">{{ item.filename }}</li>
    </ul>
  </div>
</template>

<script>
import {getAll} from "@/common/user-service";

export default {
  data() {
    return {
      searchTerm: '', // Arama terimi için kullanılacak değişken
      items: [ /* Arama yapılacak öğeler */ ],
    };
  },
  computed: {
    filteredItems() {
      // Arama terimine göre filtreleme işlemi
      return this.items.filter(item =>
          item.filename.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    },
  },
  created() {
    this.jwt = localStorage.getItem('accessToken');
    const customHeaders = {
      'Authorization': "Bearer " + this.jwt,

    };

    this.getfilelists(customHeaders);

  },
  methods: {

    async getfilelists(customHeaders) {
      try {


        const response = await getAll(customHeaders);
        this.items = response.data;
          console.log(this.items);
        // Paralel olarak istekleri yap
        await Promise.all(this.files
            .filter(file => !file.trashCanFiles)
            .map(file => this.ListFiles(file.filename, file.fileDetail.mediType, file)));

      } catch (error) {
        console.error('Dosya listesi alınamadı:', error);
      }
    }
    ,



  }

};
</script>

<style scoped>

</style>