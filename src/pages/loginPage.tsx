import React, { FormEvent, useEffect, useState } from "react";
import {useLocation, useNavigate } from "react-router-dom";
import { useLoginQuery } from "../store/api/serverApi";
import { useActions } from "../hooks/actions";
import CButton from "../components/UI/CButton/CButton";
import FormField from "../components/UI/FormField/FormField";
import { TextField, Button, Box, FormControl, IconButton, Input, InputAdornment, InputLabel, FormControlLabel, Checkbox, Avatar, Container, CssBaseline, Grid, Typography, outlinedInputClasses } from "@mui/material";
import { Copyright, Visibility, VisibilityOff } from "@mui/icons-material";
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Link from '@mui/material/Link';
import { createTheme, Theme, ThemeProvider } from '@mui/material/styles';

const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathFrom = location.state?.from?.pathname;

  const { setToken, setUser, setIsError, setIsLoaded } = useActions();

  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
  const [showPassword, setShowPassword] = React.useState(false);

  const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
  };

  const { data, isLoading, isError, error } = useLoginQuery(
    {
      username,
      password,
    },
    {
      skip: !isSubmitted,
    },
  );

  useEffect(() => {
    if (!isLoading && data !== undefined) {
      setToken(data[1]);
      setIsLoaded(true);
      setIsError(false);
      setUser(data[0]);
      navigate(pathFrom || "/", { replace: true });
    }
    if (isError) {
      setIsSubmitted(false);
      setIsError(true);
    }
  }, [isLoading]);

  const onClick = (e: React.MouseEvent) => {
    setIsSubmitted(true);
  };

  const handleClickShowPassword = () => setShowPassword((show) => !show);


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
            Sign in
          </Typography>
          <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              id="username"
              label="Username"
              name="username"
              autoComplete="username"
              autoFocus    
              InputLabelProps={{ style: { color: 'white' }}}    
              InputProps={{ style: { color: 'white' } }} 
              onChange={(e) => setUsername(e.target.value)}  
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

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 , color:'white'}}
              onClick={onClick}
            >
              Sign In
            </Button>
            <Grid container >
              <Grid item sx={{ mt: 4, mb: 4 }}>
                <Link href="/register">
                  {"Нет аккаунта? Sign up"}
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
}

export default LoginPage;
