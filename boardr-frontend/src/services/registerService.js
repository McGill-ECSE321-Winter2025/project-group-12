import api from './api'

export async function registerUser(name, email, password) {
  // Validate inputs
  if (!name || !email || !password) {
    throw new Error('All fields are required.')
  }

  try {
    // Register user via API
    const userData = { name, email, password }
    const response = await api.post('/users', userData)
    const user = response.data

    // Auto-login after registration
    localStorage.setItem('user', JSON.stringify(user))
    return user
  } catch (error) {
    // Enhanced error handling
    let errorMessage = 'Registration failed. Please try again.'
    if (error.response) {
      errorMessage = error.response.data?.message || `Server error: ${error.response.data.errors}`
    } else if (error.request) {
      errorMessage = 'Network error: Could not connect to the server.'
      console.error('Request details:', error.request)
    } else {
      errorMessage = error.message
    }
    console.error('Registration error:', error)
    throw new Error(errorMessage)
  }
}