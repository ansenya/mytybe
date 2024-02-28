import React, { FormEvent, useEffect, useState } from "react";
import {useLocation, useNavigate } from "react-router-dom";
import { useLoginQuery } from "../store/api/serverApi";
import { useActions } from "../hooks/actions";
import { TextField, Button, Box, IconButton, InputAdornment, Container, CssBaseline, Grid, Typography, outlinedInputClasses } from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import {Link} from 'react-router-dom'
import { createTheme, Theme, ThemeProvider } from '@mui/material/styles';
import AuthFormField from "../components/UI/FormField/AuthFormFiled";
import customTheme from "../models/customAuthTheme";


const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathFrom = location.state?.from?.pathname;

  const { setToken, setUser, setIsError, setIsLoaded } = useActions();

  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);


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


  function handleSubmit(event: FormEvent<HTMLFormElement>): void {
    throw new Error("Function not implemented.");
  }

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

              <AuthFormField
                label="username"
                onChange={(e) => setUsername(e.target.value)}
              />

              <AuthFormField
                label="password"
                onChange={(e) => setPassword(e.target.value)}
              />

              <Button
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 , color:'white', backgroundColor: '#6002ee'}}
                onClick={onClick}
              >
                Sign In
              </Button>
              <Grid container >
                <Grid item sx={{ mt: 4, mb: 4 }}>
                  <Link to="/register">
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
