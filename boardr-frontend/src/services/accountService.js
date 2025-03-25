import api from './api'

export const accountService = {
//TODO: Define ALL CRUD methods for the class here

//EXAMPLE

  // POST /users
  async createUser(userData) {
    try {
      const response = await api.post('/users', userData)
      return response.data
    } catch (error) {
      console.error('Error creating user:', error)
      throw error
    }
  },

  // DELETE /users/{userId}
  async deleteUser(userId) {
    try {
      const response = await api.delete(`/users/${userId}`)
      return response.data
    } catch (error) {
      console.error('Error deleting user:', error)
      throw error
    }
  },
}