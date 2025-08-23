import { createStore } from "vuex"
import user from "@/store/user";

export default createStore({
  state: () => ({
    all: [],
  }),
  getters: {},
  mutations: {},
  actions: {},
  modules: {
    user,// store.state.module_name
  },
})
