import api from './api'

export async function loginUser(email, password) {
  // Validate inputs
  if (!email || !password) {
    throw new Error('Email and password are required.')
  }

  try {
    // Fetch user by email via API
    const response = await api.get(`/users/email/${email}`)
    const user = response.data

    // Check password match (in production, this should be server-side)
    if (user.password !== password) {
      throw new Error('Invalid email or password.')
    }

    // Store user in localStorage
    localStorage.setItem('user', JSON.stringify(user))
    return user
  } catch (error) {
    // Enhanced error handling
    let errorMessage = 'Login failed. Please try again.'
    if (error.response) {
      errorMessage = error.response.data?.message || `Server error: ${error.response.status}`
      if (error.response.status === 404) {
        errorMessage = 'Email not found.'
      }
    } else if (error.request) {
      errorMessage = 'Network error: Could not connect to the server.'
      console.error('Request details:', error.request)
    } else {
      errorMessage = error.message
    }
    console.error('Login error:', error)
    throw new Error(errorMessage)
  }
}