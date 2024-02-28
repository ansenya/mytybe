import React, { useEffect, useState, useMemo, FormEvent } from "react";
import { useForm, SubmitHandler, useFieldArray } from "react-hook-form";
import { useRegisterMutation } from "../store/api/serverApi";
import FormField from "../components/UI/FormField/FormField";
import CButton from "../components/UI/CButton/CButton";
import {useLocation, useNavigate } from "react-router-dom";
import { useActions } from "../hooks/actions";
import { Theme, ThemeProvider } from "@emotion/react";
import { Container, CssBaseline, Box, Typography, TextField, Button, Grid, createTheme, outlinedInputClasses, IconButton, InputAdornment } from "@mui/material";
import {Link} from 'react-router-dom'
import { Visibility, VisibilityOff } from "@mui/icons-material";
import AuthFormField from "../components/UI/FormField/AuthFormFiled";
import customTheme from "../models/customAuthTheme";


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
              
              <AuthFormField
                label="username"
                onChange={(e) => setUsername(e.target.value)}
              />

              <AuthFormField
                label="password"
                onChange={(e) => setPassword(e.target.value)}
              />

              <AuthFormField
                label="name"
              />

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 , backgroundColor: '#6002ee'}}
            >
              Sign In
            </Button>
            <Grid container >
              <Grid item sx={{ mt: 4, mb: 4 }}>
                <Link to="/login">
                  {"Уже есть аккаунт? Sing in"}
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
