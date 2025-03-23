<template>
    <Menubar :model="computedMenuItems" class="bg-[#181818] text-[#e0e0e0] border-none" />
  </template>
  
  <script>
  import Menubar from 'primevue/menubar'
  
  export default {
    name: 'MenuBarItem',
    components: { Menubar },
    props: {
      items: {
        type: Array,
        required: true,
      },
      isLoggedIn: {
        type: Boolean,
        default: false,
      },
    },
    computed: {
      computedMenuItems() {
        // Base items from props
        const baseItems = this.items.map(item => ({
          label: item.label,
          icon: item.icon,
          command: item.command,
          items: item.subItems
            ? item.subItems.map(sub => ({
                label: sub.label,
                icon: sub.icon,
                command: sub.command,
              }))
            : undefined,
        }))
  
        // Add "Login" item if not logged in
        if (!this.isLoggedIn) {
          baseItems.push({
            label: 'Login',
            icon: 'pi pi-sign-in',
            command: () => this.$router.push('/login'),
          })
        }
  
        return baseItems
      },
    },
  }
  </script>
  
  <style scoped>
  :deep(.p-menubar) {
    background: #181818;
    border: none;
    border-radius: 0;
  }
  
  :deep(.p-menuitem-link) {
    color: #e0e0e0;
    transition: color 0.2s ease;
  }
  
  :deep(.p-menuitem-link:hover) {
    color: #1ed760;
    background: rgba(255, 255, 255, 0.1);
  }
  
  :deep(.p-submenu-list) {
    background: #181818;
    border: none;
  }
  </style>