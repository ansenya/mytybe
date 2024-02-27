import React, { useEffect, useState, useMemo, FormEvent } from "react";
import { useForm, SubmitHandler, useFieldArray } from "react-hook-form";
import { useRegisterMutation } from "../store/api/serverApi";
import FormField from "../components/UI/FormField/FormField";
import CButton from "../components/UI/CButton/CButton";
import {useLocation, useNavigate } from "react-router-dom";
import { useActions } from "../hooks/actions";
import { Theme, ThemeProvider } from "@emotion/react";
import { Container, CssBaseline, Box, Typography, TextField, Button, Grid, createTheme, outlinedInputClasses, IconButton, InputAdornment } from "@mui/material";
import Link from '@mui/material/Link';
import { Visibility, VisibilityOff } from "@mui/icons-material";


export interface RegisterArgs {
  username: string;
  password: string;
  name?: string;
  surname?: string;
  sex?: "man" | "woman";
  age?: number;
}

const RegistrationPage = () => {
  const navigate = useNavigate()
  const [post, { data, isLoading, isError, error}] = useRegisterMutation();
  const { register, formState } = useForm<RegisterArgs>();
  const submit: SubmitHandler<RegisterArgs> = (data) => {
    post(data);
  };
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
  const [showPassword, setShowPassword] = React.useState(false);

  useEffect(() => {
    if (data){
      navigate("/login", {replace: true})
    } 
  }, [data])

  const handleClickShowPassword = () => setShowPassword((show) => !show);


  const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
  };



  function handleSubmit(event: FormEvent<HTMLFormElement>): void {
    throw new Error("Function not implemented.");
  }

  const customTheme = (outerTheme: Theme) =>
  createTheme({
    palette: {
      background: {
        default: 'primary', 
      },
    },
    components: {
      MuiTextField: {
        styleOverrides: {
          root: {
            '--TextField-brandBorderColor': 'white',
            '--TextField-brandBorderHoverColor': 'white',
            '--TextField-brandBorderFocusedColor': 'white',
            '& label.Mui-focused': {
              color: 'white',
            },
          },
        },
      },
      MuiOutlinedInput: {
        styleOverrides: {
          notchedOutline: {
            borderColor: 'var(--TextField-brandBorderColor)',
          },
          root: {
            [`&:hover .${outlinedInputClasses.notchedOutline}`]: {
              borderColor: 'var(--TextField-brandBorderHoverColor)',
            },
            [`&.Mui-focused .${outlinedInputClasses.notchedOutline}`]: {
              borderColor: 'var(--TextField-brandBorderFocusedColor)',
            },
          },
        },
      },
      MuiFilledInput: {
        styleOverrides: {
          root: {
            '&::before, &::after': {
              borderBottom: '2px solid var(--TextField-brandBorderColor)',
            },
            '&:hover:not(.Mui-disabled, .Mui-error):before': {
              borderBottom: '2px solid var(--TextField-brandBorderHoverColor)',
            },
            '&.Mui-focused:after': {
              borderBottom: '2px solid var(--TextField-brandBorderFocusedColor)',
            },
          },
        },
      },
      MuiInput: {
        styleOverrides: {
          root: {
            '&::before': {
              borderBottom: '2px solid var(--TextField-brandBorderColor)',
            },
            '&:hover:not(.Mui-disabled, .Mui-error):before': {
              borderBottom: '2px solid var(--TextField-brandBorderHoverColor)',
            },
            '&.Mui-focused:after': {
              borderBottom: '2px solid var(--TextField-brandBorderFocusedColor)',
            },
            
          },
        },
      },
    },
  });

  
  return (
    <ThemeProvider theme={customTheme}>
      <Container sx={{backgroundColor: '#030014'}}component="main" maxWidth="xs">
        <CssBaseline />
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Typography component="h1" variant="h4" sx={{ mt: 3, mb: 1 , color: 'white'}}>
            Sign up
          </Typography>
          <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
            <TextField
              margin="normal"
              required
              fullWidth
              id="username"
              label="Username"
              name="username"
              autoComplete="username"
              autoFocus
              InputLabelProps={{ style: { color: 'white' }}} 
            />
           <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              name="password"
              label="Password"
              type={showPassword ? 'text' : 'password'}
              id="password"
              autoComplete="current-password"
              InputLabelProps={{ style: { color: 'white' } }}
              InputProps={{
                style: { color: 'white' },
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      onMouseDown={handleMouseDownPassword}
                      style={{ color: 'white' }}
                    >
                      {showPassword ? <Visibility /> : <VisibilityOff />}
                    </IconButton>
                  </InputAdornment>
                )
              }}
              onChange={(e) => setPassword(e.target.value)}
            />
             <TextField
              margin="normal"
              required
              fullWidth
              id="name"
              label="Name"
              name="name"
              autoComplete="name"
              autoFocus
              InputLabelProps={{ style: { color: 'white' }}} 
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Sign In
            </Button>
            <Grid container >
              <Grid item sx={{ mt: 4, mb: 4 }}>
                <Link href="/login">
                  {"Уже есть аккаунт? Войти"}
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
};

export default RegistrationPage;
